package com.ex.erp.config.jwt;

import com.ex.erp.service.security.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private TokenService tokenService;
    private UserDetailsService userDetailsService;
    
    @Autowired
    public void setTokenService(TokenService tokenService){
        this.tokenService = tokenService;
    }
    @Autowired
    public void setTokenService(UserDetailsService userDetailsService){
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        if(token != null){
            String accessToken = token.replace("Bearer ", "");
            Map<String, Object> tokenDetail = tokenService.parseToken(accessToken);
            String username = (String) tokenDetail.get("username");
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }
}
