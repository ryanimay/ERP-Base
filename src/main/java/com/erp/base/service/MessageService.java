package com.erp.base.service;

import com.erp.base.model.MessageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
/**
 * 統一用來發送websocket信息
 * */
@Service
public class MessageService {
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    public void setSimpMessagingTemplate(SimpMessagingTemplate messagingTemplate){
        this.messagingTemplate = messagingTemplate;
    }

    public void send(MessageModel message){
        messagingTemplate.convertAndSend(message.getTopic(), message);
    }

    public void sendTo(MessageModel message){
        messagingTemplate.convertAndSendToUser(message.getTo(), message.getTopic(), message);
    }
}

