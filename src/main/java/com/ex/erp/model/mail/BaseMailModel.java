package com.ex.erp.model.mail;

public abstract class BaseMailModel {
    protected final String subjectName;
    protected final String mailTemplatePath;
    public BaseMailModel(String subjectName, String mailTemplatePath) {
        this.subjectName = subjectName;
        this.mailTemplatePath = mailTemplatePath;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public String getMailTemplatePath() {
        return mailTemplatePath;
    }
}
