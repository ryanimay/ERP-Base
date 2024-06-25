package com.erp.base.config.websocket;

public interface WebsocketConstant {
    interface FROM{
        String SYSTEM = "system";
    }

    interface TOPIC {
        String PREFIX = "/topic";
        String NOTIFICATION = PREFIX + "/notification";
        String CLIENT_STATUS = PREFIX + "/clientStatus";
    }

    interface DESTINATION{
        String PREFIX = "/app";
        String USER = "/user";
    }
}
