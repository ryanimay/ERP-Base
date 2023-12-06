package com.ex.erp.config.security;

import com.ex.erp.dto.response.ApiResponseCode;
import com.ex.erp.dto.response.FilterExceptionResponse;
import com.ex.erp.filter.jwt.JwtAuthenticationFilter;
import com.ex.erp.model.PermissionModel;
import com.ex.erp.service.CacheService;
import com.ex.erp.service.cache.ClientCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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

import java.util.Collections;
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
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter authFilter) throws Exception {
        cacheService.refreshAllCache();//啟動時刷新全部緩存

        //配置資料庫內permission表的所有API權限設定
        configurePermission(http);

        //permission表以外的設定
        http.authorizeHttpRequests(request -> request
                        .anyRequest().authenticated())
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exception ->
                        exception.accessDeniedHandler((request, response, accessDeniedException) ->
                                FilterExceptionResponse.error(response, ApiResponseCode.ACCESS_DENIED))
                                .authenticationEntryPoint((request, response, authException) ->
                                        FilterExceptionResponse.error(response, ApiResponseCode.INVALID_SIGNATURE)));

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //動態設定所有權限
    private void configurePermission(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(request -> {
            List<PermissionModel> permissions = clientCache.getPermission();//寫在每次的請求內，這樣才可以靠刷新緩存改變權限
            Collections.reverse(permissions);//調頭，從子權限開始設定，不然會被父權限擋掉
            for (PermissionModel permission : permissions) {
                String authority = permission.getAuthority();
                String url = permission.getUrl();
                if("*".equals(authority)){
                    request.requestMatchers(url).permitAll();
                }else{
                    //每個權限節點包含所有父節點都可以通過
                    request.requestMatchers(antMatcher(url)).hasAnyAuthority(permission.getAuthoritiesIncludeParents().toArray(new String[0]));
                }
            }
        });
    }
}
