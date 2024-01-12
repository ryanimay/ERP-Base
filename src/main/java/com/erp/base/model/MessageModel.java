package com.erp.base.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageModel {
    private String from;
    private String to;
    private String topic;
    private Object data;

    public MessageModel(String from, String topic, Object data) {
        this.from = from;
        this.topic = topic;
        this.data = data;
    }
}
