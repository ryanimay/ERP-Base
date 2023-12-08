package com.erp.base.model.mail;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:mail-template.properties")
public class ResetPasswordModel extends BaseMailModel{
    public ResetPasswordModel(@Value("${mail.resetPassword.subject}") String subject,
                              @Value("${mail.resetPassword.templatePath}") String mailTemplatePath) {
        super(subject, mailTemplatePath);
    }
}
