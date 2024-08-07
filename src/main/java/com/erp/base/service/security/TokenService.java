package com.erp.base.service.security;

import com.erp.base.model.constant.response.ApiResponseCode;
import com.erp.base.model.dto.request.client.LoginRequest;
import com.erp.base.model.dto.request.permission.SecurityConfirmRequest;
import com.erp.base.model.dto.response.ApiResponse;
import com.erp.base.model.entity.ClientModel;
import com.erp.base.service.ClientService;
import com.erp.base.tool.ObjectTool;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
public class TokenService {
    @Value("${security.password}")
    private String securityPassword;
    private KeyPair keyPair;
    private AuthenticationProvider authenticationProvider;
    private ClientService clientService;
    public static final String ACCESS_TOKEN = "X-Access-Token";
    public static final int ACCESS_TOKEN_EXPIRE_TIME = 60 * 30;//30分鐘刷新(秒為單位)
    public static final String REFRESH_TOKEN = "X-Refresh-Token";
    public static final int REFRESH_TOKEN_EXPIRE_TIME = 60 * 60 * 6;//6hr(秒為單位)
    public static final String TOKEN_PROPERTIES_UID = "uid";
    public static final String TOKEN_PREFIX = "Bearer ";

    @Autowired
    public void setAuthenticationProvider(@Lazy AuthenticationProvider authenticationProvider) {
        this.authenticationProvider = authenticationProvider;
    }

    @Autowired
    public void setClientService(@Lazy ClientService clientService) {
        this.clientService = clientService;
    }

    @PostConstruct
    public void init(){
        keyPair = Keys.keyPairFor(SignatureAlgorithm.RS256); //做非對稱，伺服器重啟時刷新
    }

    public HttpHeaders createToken(LoginRequest request){
        Boolean rememberMe = request.getRememberMe();
        String username = request.getUsername();
        // 封裝帳密
        Authentication authentication = new UsernamePasswordAuthenticationToken(username, request.getPassword());
        // security執行帳密認證
        authenticationProvider.authenticate(authentication);
        ClientModel clientByUsername = clientService.findByUsername(username);
        // 產token
        HttpHeaders httpHeaders = new HttpHeaders();
        String accessToken = createToken(ACCESS_TOKEN, clientByUsername.getId(), ACCESS_TOKEN_EXPIRE_TIME);
        httpHeaders.add(HttpHeaders.AUTHORIZATION, TOKEN_PREFIX + accessToken);
        //rememberMe才發refreshToken
        if(Boolean.TRUE.equals(rememberMe)) {
            String refreshToken = createToken(REFRESH_TOKEN, clientByUsername.getId(), REFRESH_TOKEN_EXPIRE_TIME);
            httpHeaders.add(REFRESH_TOKEN, refreshToken);
        }
        return httpHeaders;
    }

    //公鑰解密
    public Map<String,Object> parseToken(String token){
        checkJWTHeader(token);
        PublicKey publicKey = keyPair.getPublic();
        Claims claims = Jwts.parserBuilder().setSigningKey(publicKey).build().parseClaimsJws(token).getBody();
        return new HashMap<>(claims);
    }

    private void checkJWTHeader(String token) {
        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            throw new SignatureException("Invalid JWT token format");
        }
        String headerJson = new String(Base64.getUrlDecoder().decode(parts[0]));
        Map<String, Object> header = ObjectTool.fromJson(headerJson);

        // 確保簽名方式是RS256
        if (!SignatureAlgorithm.RS256.getValue().equals(header.get("alg"))) {
            throw new SignatureException("Invalid JWT signature algorithm");
        }
    }

    //私鑰加密
    public String createToken(String type, Long uid, int expirationTime) {
        //轉毫秒
        long expirationMillis = getExpireMillisecond(expirationTime);

        // 設置標準內容與自定義內容
        Claims claims = Jwts.claims();
        claims.setSubject(type);
        claims.setIssuedAt(new Date());
        claims.setExpiration(new Date(expirationMillis));
        claims.put(TOKEN_PROPERTIES_UID, uid);

        // 簽名後產生 token
        PrivateKey privateKey = keyPair.getPrivate();
        return Jwts.builder()
                .setClaims(claims)
                .signWith(privateKey, SignatureAlgorithm.RS256) // 明確指定使用RS256算法
                .compact();
    }

    private Long getExpireMillisecond(int expirationTime){
        return Instant.now()
                .plusSeconds(expirationTime)
                .getEpochSecond()
                * 1000;
    }

    public ResponseEntity<ApiResponse> getPublicKey(SecurityConfirmRequest request) {
        if (securityPassword.equals(request.getSecurityPassword())){
            PublicKey publicKey = keyPair.getPublic();
            return ApiResponse.success(Base64.getEncoder().encodeToString(publicKey.getEncoded()));
        }
        return ApiResponse.error(ApiResponseCode.SECURITY_ERROR, false);
    }
}
