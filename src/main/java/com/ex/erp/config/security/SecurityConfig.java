package com.ex.erp.config.security;

import com.ex.erp.filter.jwt.JwtAuthenticationFilter;
import com.ex.erp.model.PermissionModel;
import com.ex.erp.service.cache.ClientCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private ClientCache clientCache;

    @Autowired
    public void setClientCache(@Lazy ClientCache clientCache) {
        this.clientCache = clientCache;
    }

    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter authFilter) throws Exception {

        List<PermissionModel> permissions = clientCache.getPermission();
        //配置資料庫內所有permission表的API權限設定
        for (PermissionModel permission : permissions) {
            configurePermission(http, permission);
        }

        //權限以外的設定
        http.authorizeHttpRequests(request -> request
                        .anyRequest().authenticated())
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //設定所有權限
    private void configurePermission(HttpSecurity http, PermissionModel permission) throws Exception {
        String authority = permission.getAuthority();
        System.out.println("set permission: " + permission.getUrl() + " : " + permission.getAuthority());
        if("*".equals(authority)){
            http.authorizeHttpRequests(request -> request.requestMatchers(permission.getUrl()).permitAll());
        }else{
            http.authorizeHttpRequests(request -> request.requestMatchers(permission.getUrl()).hasAuthority(authority));
        }
    }
}
