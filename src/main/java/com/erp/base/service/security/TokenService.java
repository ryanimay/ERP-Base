package com.erp.base.service.security;

import com.erp.base.model.dto.request.client.LoginRequest;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
public class TokenService {
    private Key secretKey;
    private JwtParser jwtParser;
    private AuthenticationProvider authenticationProvider;
    public static final String ACCESS_TOKEN = "X-Access-Token";
    private static final int ACCESS_TOKEN_EXPIRE_TIME = 60 * 30;//30分鐘刷新(秒為單位)
    public static final String REFRESH_TOKEN = "X-Refresh-Token";
    private static final int REFRESH_TOKEN_EXPIRE_TIME = 60 * 60 * 6;//6hr(秒為單位)
    public static final String TOKEN_PROPERTIES_USERNAME = "username";

    @Autowired
    public void setAuthenticationProvider(@Lazy AuthenticationProvider authenticationProvider) {
        this.authenticationProvider = authenticationProvider;
    }

    @PostConstruct
    public void init(){
        secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);//伺服器重啟時刷新
        jwtParser = Jwts.parserBuilder().setSigningKey(secretKey).build();
    }

    public HttpHeaders createToken(LoginRequest request){
        // 封裝帳密
        Authentication authentication = new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());
        // security執行帳密認證
        authentication = authenticationProvider.authenticate(authentication);
        // 認證成功後取得結果
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        // 產token
        String accessToken = createToken(ACCESS_TOKEN, userDetails.getUsername(), ACCESS_TOKEN_EXPIRE_TIME);
        String refreshToken = createToken(REFRESH_TOKEN, userDetails.getUsername(), REFRESH_TOKEN_EXPIRE_TIME);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        httpHeaders.add(REFRESH_TOKEN, refreshToken);
        return httpHeaders;
    }

    public String refreshAccessToken(String refreshToken){
        Map<String, Object> payload = parseToken(refreshToken);
        String username = (String) payload.get(TOKEN_PROPERTIES_USERNAME);
        return createToken(REFRESH_TOKEN, username, ACCESS_TOKEN_EXPIRE_TIME);
    }

    public Map<String,Object> parseToken(String token){
        Claims claims = jwtParser.parseClaimsJws(token).getBody();
        return new HashMap<>(claims);
    }

    public String createToken(String type, String username, int expirationTime) {
        //轉毫秒
        long expirationMillis = getExpireMillisecond(expirationTime);

        // 設置標準內容與自定義內容
        Claims claims = Jwts.claims();
        claims.setSubject(type);
        claims.setIssuedAt(new Date());
        claims.setExpiration(new Date(expirationMillis));
        claims.put(TOKEN_PROPERTIES_USERNAME, username);

        // 簽名後產生 token
        return Jwts.builder()
                .setClaims(claims)
                .signWith(secretKey)
                .compact();
    }

    private Long getExpireMillisecond(int expirationTime){
        return Instant.now()
                .plusSeconds(expirationTime)
                .getEpochSecond()
                * 1000;
    }
}
