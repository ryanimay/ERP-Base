package com.erp.base.tool;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.iv.RandomIvGenerator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class JasyptEncodeTest {

    @Test
    @DisplayName("測試Jasypt加密解密")
    void jasyptEncodeDecode() {
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        //jasypt解密時默認是用RandomIvGenerator做向量，但這邊預設是null所以要指定
        encryptor.setIvGenerator(new RandomIvGenerator());
        encryptor.setPassword("testEncode"); // 加密密鑰

        String originUrl = "testUrl";
        String originUsername = "testUsername";
        String originPassword = "testPassword";
        String originMailUsername = "testMailUsername";
        String originMailPassword = "testMailPassword";

        String dbUrl = encryptor.encrypt(originUrl);
        String dbUsername = encryptor.encrypt(originUsername);
        String dbPassword = encryptor.encrypt(originPassword);
        String mailUsername = encryptor.encrypt(originMailUsername);
        String mailPassword = encryptor.encrypt(originMailPassword);

        System.out.println("加密----------------------------------------");
        System.out.println("Encrypted text: [" + dbUrl + "]");
        System.out.println("Username text: [" + dbUsername + "]");
        System.out.println("Password text: [" + dbPassword + "]");
        System.out.println("MailUsername text: [" + mailUsername + "]");
        System.out.println("MailPassword text: [" + mailPassword + "]");

        Assertions.assertEquals(originUrl, encryptor.decrypt(dbUrl));
        Assertions.assertEquals(originUsername, encryptor.decrypt(dbUsername));
        Assertions.assertEquals(originPassword, encryptor.decrypt(dbPassword));
        Assertions.assertEquals(originMailUsername, encryptor.decrypt(mailUsername));
        Assertions.assertEquals(originMailPassword, encryptor.decrypt(mailPassword));
    }
}
