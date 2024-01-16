package com.erp.base.model.mail;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:mail-template.properties")
public class SalaryMailModel extends BaseMailModel{
    public SalaryMailModel(@Value("${mail.salary.subject}") String subject,
                           @Value("${mail.salary.templatePath}") String mailTemplatePath) {
        super(subject, mailTemplatePath);
    }
}
