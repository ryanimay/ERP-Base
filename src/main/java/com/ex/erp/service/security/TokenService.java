package com.ex.erp.service.security;

import com.ex.erp.dto.request.LoginRequest;
import com.ex.erp.dto.response.LoginResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class TokenService {
    private Key secretKey;
    private JwtParser jwtParser;
    private AuthenticationProvider authenticationProvider;

    @Autowired
    public void setAuthenticationProvider(AuthenticationProvider authenticationProvider) {
        this.authenticationProvider = authenticationProvider;
    }

    @PostConstruct
    public void init(){
        secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);//伺服器重啟時刷新
        jwtParser = Jwts.parserBuilder().setSigningKey(secretKey).build();
    }

    public LoginResponse createToken(LoginRequest request){
        // 封裝帳密
        Authentication authToken = new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());
        // security執行帳密認證
        authToken = authenticationProvider.authenticate(authToken);
        // 認證成功後取得結果
        UserDetails userDetails = (UserDetails) authToken.getPrincipal();
        // 產token
        String accessToken = createToken("Access Token", userDetails.getUsername(), 90);//90秒
        String refreshToken = createToken("Refresh Token", userDetails.getUsername(), 60 * 30);//30分鐘刷新
        return new LoginResponse(accessToken, refreshToken);
    }

    public String refreshAccessToken(String refreshToken){
        Map<String, Object> payload = parseToken(refreshToken);
        String username = (String) payload.get("username");
        return createToken("Access Token", username, 90);
    }

    public Map<String,Object> parseToken(String token){
        Claims claims = jwtParser.parseClaimsJws(token).getBody();
        return new HashMap<>(claims);
    }

    private String createToken(String tokenType, String username, int expirationTime) {
        //轉毫秒
        long expirationMillis = Instant.now()
                .plusSeconds(expirationTime)
                .getEpochSecond()
                * 1000;

        // 設置標準內容與自定義內容
        Claims claims = Jwts.claims();
        claims.setSubject(tokenType);
        claims.setIssuedAt(new Date());
        claims.setExpiration(new Date(expirationMillis));
        claims.put("username", username);

        // 簽名後產生 token
        return Jwts.builder()
                .setClaims(claims)
                .signWith(secretKey)
                .compact();
    }
}
