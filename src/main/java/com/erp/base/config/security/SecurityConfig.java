package com.erp.base.config.security;

import com.erp.base.filter.jwt.DenyPermissionFilter;
import com.erp.base.filter.jwt.JwtAuthenticationFilter;
import com.erp.base.filter.jwt.UserStatusFilter;
import com.erp.base.model.constant.response.ApiResponseCode;
import com.erp.base.model.dto.response.FilterExceptionResponse;
import com.erp.base.model.entity.PermissionModel;
import com.erp.base.service.CacheService;
import com.erp.base.service.PermissionService;
import com.erp.base.service.security.TokenService;
import com.erp.base.tool.LogFactory;
import com.erp.base.tool.ObjectTool;
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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    LogFactory LOG = new LogFactory(SecurityConfig.class);
    private final PermissionService permissionService;
    private final TokenService tokenService;
    private final CacheService cacheService;
    //不須驗證權限的公開url
    public static Set<String> noRequiresAuthenticationSet = new HashSet<>();
    @Autowired
    public SecurityConfig(PermissionService permissionService, CacheService cacheService, TokenService tokenService) {
        this.permissionService = permissionService;
        this.cacheService = cacheService;
        this.tokenService = tokenService;
    }

    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        //配置資料庫內permission表的所有API權限設定
        configurePermission(http);

        //permission表以外的設定
        http.authorizeHttpRequests(request -> request
                        .requestMatchers("/swagger/swagger-ui.html", "/swagger/swagger-ui/**", "/swagger/api-docs/**").permitAll()
                        .requestMatchers("/ws/**").permitAll()
                        .anyRequest().authenticated())
                .csrf(AbstractHttpConfigurer::disable)
                //按順序:
                //JwtAuthenticationFilter: 解析header，驗證token和設置lang(後續所有請求返回語系都會依照這裡)
                //DenyPermissionFilter: 驗證路徑權限
                //UserStatusFilter: 驗證用戶狀態
                //UsernamePasswordAuthenticationFilter: SpringSecurity預設驗證，但這邊因為手動驗證，所以基本用不到
                //順序是JwtAuthenticationFilter -> DenyPermissionFilter -> UserStatusFilter -> UsernamePasswordAuthenticationFilter
                .addFilterBefore(new JwtAuthenticationFilter(tokenService, cacheService), UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(new DenyPermissionFilter(cacheService), JwtAuthenticationFilter.class)
                .addFilterAfter(new UserStatusFilter(), JwtAuthenticationFilter.class)
                .exceptionHandling(exception ->
                        exception.accessDeniedHandler((request, response, accessDeniedException) -> FilterExceptionResponse.error(response, ApiResponseCode.ACCESS_DENIED))
                                .authenticationEntryPoint((request, response, authException) -> {
                                    if(authException instanceof LockedException){
                                        FilterExceptionResponse.error(response, ApiResponseCode.CLIENT_LOCKED);
                                    }else if(authException instanceof DisabledException){
                                        FilterExceptionResponse.error(response, ApiResponseCode.CLIENT_DISABLED);
                                    }else{
                                        FilterExceptionResponse.error(response, ApiResponseCode.ACCESS_DENIED);
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
        List<PermissionModel> permissions = permissionService.findAll();
        LOG.info("all permission: {0}", ObjectTool.toJson(permissions));
        http.authorizeHttpRequests(request -> {
            for (PermissionModel permission : permissions) {
                String authority = permission.getAuthority();
                String url = permission.getUrl();
                if("*".equals(authority)){
                    request.requestMatchers(url).permitAll();//公開api
                    noRequiresAuthenticationSet.add(url);
                }else{
                    request.requestMatchers(antMatcher(url)).hasAuthority(authority);
                }
            }
        });
    }
}
