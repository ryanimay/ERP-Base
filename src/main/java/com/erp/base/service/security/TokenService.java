package com.erp.base.service.security;

import com.erp.base.model.dto.request.client.LoginRequest;
import io.jsonwebtoken.Claims;
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

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
public class TokenService {
    private KeyPair keyPair;
    private AuthenticationProvider authenticationProvider;
    public static final String ACCESS_TOKEN = "X-Access-Token";
    public static final int ACCESS_TOKEN_EXPIRE_TIME = 60 * 30;//30分鐘刷新(秒為單位)
    public static final String REFRESH_TOKEN = "X-Refresh-Token";
    public static final int REFRESH_TOKEN_EXPIRE_TIME = 60 * 60 * 6;//6hr(秒為單位)
    public static final String TOKEN_PROPERTIES_USERNAME = "username";
    public static final String TOKEN_PREFIX = "Bearer ";

    @Autowired
    public void setAuthenticationProvider(@Lazy AuthenticationProvider authenticationProvider) {
        this.authenticationProvider = authenticationProvider;
    }

    @PostConstruct
    public void init(){
        keyPair = Keys.keyPairFor(SignatureAlgorithm.RS256); //做非對稱，伺服器重啟時刷新
    }

    public HttpHeaders createToken(LoginRequest request){
        Boolean rememberMe = request.getRememberMe();
        // 封裝帳密
        Authentication authentication = new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());
        // security執行帳密認證
        authentication = authenticationProvider.authenticate(authentication);
        // 認證成功後取得結果
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        // 產token
        HttpHeaders httpHeaders = new HttpHeaders();
        String accessToken = createToken(ACCESS_TOKEN, userDetails.getUsername(), ACCESS_TOKEN_EXPIRE_TIME);
        httpHeaders.add(HttpHeaders.AUTHORIZATION, TOKEN_PREFIX + accessToken);
        //rememberMe才發refreshToken
        if(rememberMe != null && rememberMe) {
            String refreshToken = createToken(REFRESH_TOKEN, userDetails.getUsername(), REFRESH_TOKEN_EXPIRE_TIME);
            httpHeaders.add(REFRESH_TOKEN, refreshToken);
        }
        return httpHeaders;
    }

    //公鑰解密
    public Map<String,Object> parseToken(String token){
        PublicKey publicKey = keyPair.getPublic();
        Claims claims = Jwts.parserBuilder().setSigningKey(publicKey).build().parseClaimsJws(token).getBody();
        return new HashMap<>(claims);
    }

    //私鑰加密
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
        PrivateKey privateKey = keyPair.getPrivate();
        return Jwts.builder()
                .setClaims(claims)
                .signWith(privateKey)
                .compact();
    }

    private Long getExpireMillisecond(int expirationTime){
        return Instant.now()
                .plusSeconds(expirationTime)
                .getEpochSecond()
                * 1000;
    }
}
