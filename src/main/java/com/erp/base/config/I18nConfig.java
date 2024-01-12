package com.erp.base.config;

import com.erp.base.model.dto.response.ApiResponse;
import com.erp.base.model.ClientIdentity;
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
        ApiResponse.setMessageResource(messageSource);//設置給ApiResponse使用
        return messageSource;
    }
}
