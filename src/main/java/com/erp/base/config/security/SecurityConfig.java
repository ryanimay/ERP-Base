package com.erp.base.config.security;

import com.erp.base.enums.response.ApiResponseCode;
import com.erp.base.filter.jwt.DenyPermissionFilter;
import com.erp.base.filter.jwt.JwtAuthenticationFilter;
import com.erp.base.filter.jwt.UserStatusFilter;
import com.erp.base.model.dto.response.FilterExceptionResponse;
import com.erp.base.model.entity.PermissionModel;
import com.erp.base.service.PermissionService;
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
    //不須驗證權限的公開url
    public static Set<String> noRequiresAuthenticationSet = new HashSet<>();
    @Autowired
    public SecurityConfig(PermissionService permissionService) {
        this.permissionService = permissionService;
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
        //配置資料庫內permission表的所有API權限設定
        configurePermission(http);

        //permission表以外的設定
        http.authorizeHttpRequests(request -> request
                        .requestMatchers("/swagger/swagger-ui.html", "/swagger/swagger-ui/**", "/swagger/api-docs/**").permitAll()
                        .anyRequest().authenticated())
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(denyPermissionFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(authFilter, DenyPermissionFilter.class)
                .addFilterAfter(userStatusFilter, JwtAuthenticationFilter.class)
                .exceptionHandling(exception ->
                        exception.accessDeniedHandler((request, response, accessDeniedException) ->
                                FilterExceptionResponse.error(response, ApiResponseCode.ACCESS_DENIED))
                                .authenticationEntryPoint((request, response, authException) -> {
                                    authException.printStackTrace();
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
