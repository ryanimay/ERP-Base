package com.erp.base.service;

import com.erp.base.model.mail.BaseMailModel;
import com.erp.base.model.mail.FileModel;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class MailService {
    private JavaMailSender mailSender;
    private TemplateEngine templateEngine;
    @Autowired
    public void setMailSender(JavaMailSender mailSender){
        this.mailSender = mailSender;
    }
    @Autowired
    public void setTemplateEngine(TemplateEngine templateEngine){
        this.templateEngine = templateEngine;
    }

    public void sendMail(String address, BaseMailModel mailModel, Context context, FileModel fileModel) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

        helper.setTo(address);
        helper.setSubject(mailModel.getSubjectName());
        String tmp = templateEngine.process(mailModel.getMailTemplatePath(), context);
        helper.setText(tmp, true);
        //添加附件
        if(fileModel != null){
                helper.addAttachment(fileModel.getFileName(), fileModel.getDataSource());
        }

        mailSender.send(mimeMessage);
    }

    /**
     * 創建模板參數
     * 必續按照傳入順序
     * */
    public Context createContext(Object... data){
        Context context = new Context();//模板使用的參數
        context.setVariable("data", data);
        return context;
    }
}
