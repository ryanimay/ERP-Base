package com.ex.erp.config;

import com.ex.erp.dto.security.ClientIdentity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;

@Configuration
public class I18nConfig {
    //Valid提示字i18n
    @Bean
    public ResourceBundleMessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("message");
        messageSource.setDefaultLocale(ClientIdentity.defaultLocale);
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }
}
