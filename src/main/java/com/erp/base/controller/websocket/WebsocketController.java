package com.erp.base.controller.websocket;

import com.erp.base.config.websocket.WebsocketConstant;
import com.erp.base.service.WebsocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
public class WebsocketController {

    private final WebsocketService websocketService;

    @Autowired
    public WebsocketController(WebsocketService websocketService) {
        this.websocketService = websocketService;
    }

    @MessageMapping(WebsocketConstant.DESTINATION.SEND_NOTIFICATION)
    public void getNotification(){
        websocketService.sendNotification();
    }
}
