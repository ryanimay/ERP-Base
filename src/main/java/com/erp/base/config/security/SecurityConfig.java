package com.erp.base.config.security;

import com.erp.base.dto.response.FilterExceptionResponse;
import com.erp.base.enums.response.ApiResponseCode;
import com.erp.base.filter.jwt.DenyPermissionFilter;
import com.erp.base.filter.jwt.JwtAuthenticationFilter;
import com.erp.base.filter.jwt.UserStatusFilter;
import com.erp.base.model.PermissionModel;
import com.erp.base.service.CacheService;
import com.erp.base.service.cache.ClientCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
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

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private CacheService cacheService;
    private ClientCache clientCache;

    @Autowired
    public void setClientCache(ClientCache clientCache) {
        this.clientCache = clientCache;
    }
    @Autowired
    public void setCacheService(CacheService cacheService) {
        this.cacheService = cacheService;
    }

    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter authFilter, UserStatusFilter userStatusFilter, DenyPermissionFilter denyPermissionFilter) throws Exception {
        cacheService.refreshAllCache();//啟動時刷新全部緩存

        //配置資料庫內permission表的所有API權限設定
        configurePermission(http);

        //permission表以外的設定
        http.authorizeHttpRequests(request -> request
                        .anyRequest().authenticated())
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(denyPermissionFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(authFilter, DenyPermissionFilter.class)
                .addFilterAfter(userStatusFilter, JwtAuthenticationFilter.class)
                .exceptionHandling(exception ->
                        exception.accessDeniedHandler((request, response, accessDeniedException) ->
                                FilterExceptionResponse.error(response, ApiResponseCode.ACCESS_DENIED))
                                .authenticationEntryPoint((request, response, authException) -> {
                                    if(authException instanceof LockedException){
                                        FilterExceptionResponse.error(response, ApiResponseCode.CLIENT_LOCKED);
                                    }else if(authException instanceof DisabledException){
                                        FilterExceptionResponse.error(response, ApiResponseCode.CLIENT_DISABLED);
                                    }else{
                                        FilterExceptionResponse.error(response, ApiResponseCode.INVALID_SIGNATURE);
                                    }
                                    }
                                ));

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //動態設定所有權限
    private void configurePermission(HttpSecurity http) throws Exception {
        List<PermissionModel> permissions = clientCache.getPermission();
        http.authorizeHttpRequests(request -> {
            for (PermissionModel permission : permissions) {
                String authority = permission.getAuthority();
                String url = permission.getUrl();
                if("*".equals(authority)){
                    request.requestMatchers(url).permitAll();//公開api
                }else{
                    request.requestMatchers(antMatcher(url)).hasAuthority(authority);
                }
            }
        });
    }
}
