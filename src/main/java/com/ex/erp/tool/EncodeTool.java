package com.ex.erp.tool;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class EncodeTool {
    private PasswordEncoder passwordEncoder;

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder){
        this.passwordEncoder = passwordEncoder;
    }

    public String passwordEncode(String password){
        return passwordEncoder.encode(password);
    }

    public String randomPassword(int length){
        return RandomStringUtils.random(length, true, true);
    }
}
