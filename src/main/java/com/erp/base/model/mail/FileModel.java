package com.erp.base.model.mail;

import jakarta.activation.DataSource;
import jakarta.mail.util.ByteArrayDataSource;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jxls.common.Context;
import org.jxls.util.JxlsHelper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileModel {
    private String fileName;
    private DataSource dataSource;

    public FileModel(String fileName, String classPath, Map<String, Object> formatData) throws IOException {
        Resource resource = new ClassPathResource(classPath);
        InputStream inputStream = resource.getInputStream();
        Context context = new Context(formatData);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        JxlsHelper.getInstance().setEvaluateFormulas(true).processTemplate(inputStream, outputStream, context);

        this.fileName = fileName;
        this.dataSource = new ByteArrayDataSource(outputStream.toByteArray(), "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

        //本地下載測試
//        new File("C:/Users/user/Downloads" + File.separator + fileName).getParentFile().mkdirs();
//        try (FileOutputStream fos = new FileOutputStream("C:/Users/user/Downloads" + File.separator + fileName)) {
//            outputStream.writeTo(fos);
//        }
    }
}
