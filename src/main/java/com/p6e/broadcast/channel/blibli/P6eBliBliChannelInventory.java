package com.p6e.broadcast.channel.blibli;

import com.p6e.broadcast.common.P6eHttpCommon;
import com.p6e.broadcast.common.P6eToolCommon;

import java.util.List;
import java.util.Map;

class P6eBliBliChannelInventory {

    private static final String WEB_SOCKET_URL =
            "https://api.live.bilibili.com/room/v1/Danmu/getConf?room_id=${room}&platform=pc&player=web";
    private static final String LOGIN_MESSAGE = "{\"uid\":0,\"roomid\":${room},\"protover\":2," +
            "\"platform\":\"web\",\"clientver\":\"1.10.3\",\"type\":2,\"key\":\"${token}\"}";
    private static final String PANT_MESSAGE = "[object Object]";
    private static final int PANT_TYPE_MESSAGE = 2;
    private static final int LOGIN_TYPE_MESSAGE = 7;


    private String rid;
    private String wsUrl;
    private String wssUrl;
    private String token;

    P6eBliBliChannelInventory(String rid) {
        this.rid = rid;
    }

    private synchronized void webSocketUrl() {
        if (wsUrl != null && wssUrl != null && token != null) return;
        try {
            String res = P6eHttpCommon.doGet(P6eToolCommon.translate(WEB_SOCKET_URL, "room", rid));
            Map<String, Object> map = P6eToolCommon.fromJson(res);
            if (map != null) {
                Object code = map.get("code");
                if (code != null) {
                    if (Double.valueOf(String.valueOf(code)).intValue() == 0) {
                        Map urlMap = (Map) ((List) ((Map) map.get("data")).get("host_server_list")).get(0);
                        wsUrl = "ws://" + urlMap.get("host")
                                + ":" + Double.valueOf(String.valueOf(urlMap.get("ws_port"))).intValue() + "/sub";
                        wssUrl = "wss://" + urlMap.get("host")
                                + ":" + Double.valueOf(String.valueOf(urlMap.get("wss_port"))).intValue() + "/sub";
                        token =  String.valueOf(((Map) map.get("data")).get("token"));
                    }
                }
            }
            if (wsUrl == null || wssUrl == null || token == null)
                throw new RuntimeException("P6eBliBliChannelInventory web socket url error !");
        } catch (Exception e) {
            throw new RuntimeException("P6eBliBliChannelInventory web socket url error !");
        }
    }

    String getWebSocketWsUrl() {
        if (wsUrl == null || wssUrl == null || token == null) webSocketUrl();
        return wsUrl;
    }

    String getWebSocketWssUrl() {
        if (wsUrl == null || wssUrl == null || token == null) webSocketUrl();
        return wssUrl;
    }

    String getWebSocketToken() {
        if (wsUrl == null || wssUrl == null || token == null) webSocketUrl();
        return token;
    }

    String getPantInfo() {
        return PANT_MESSAGE;
    }

    String getLoginInfo() {
        return P6eToolCommon.translate(LOGIN_MESSAGE, "room", rid, "token", this.getWebSocketToken());
    }



    int getPantType() {
        return PANT_TYPE_MESSAGE;
    }

    int getLoginType() {
        return LOGIN_TYPE_MESSAGE;
    }

}
