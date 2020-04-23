package com.p6e.broadcast.channel.huomao;

import com.p6e.broadcast.common.P6eHttpCommon;
import com.p6e.broadcast.common.P6eToolCommon;

import java.util.Map;

class P6eHuoMaoChannelInventory {


    private static final String WEB_SOCKET_URL = "http://www.huomao.com/ajax/goimConf?type=h5";
    private static final String LOGIN_MESSAGE = "{\"Uid\":0, \"Rid\":${id}}";
    private static final String PANT_MESSAGE = "";

    public final static int MESSAGE_ROOM_AGREEMENT_SEND = 1;
    public final static int MESSAGE_ROOM_AGREEMENT_ACCEPT = 0;
    private final static int LOGIN_TYPE_MESSAGE = 7;
    private static final int PANT_TYPE_MESSAGE = 2;


    private String wsUrl;
    private String wssUrl;

    private String id;

    private void webSocketUrl() {
        try {
            String res = P6eHttpCommon.doGet(WEB_SOCKET_URL);
            if (res == null || "".equals(res)) {
                Map map = P6eToolCommon.fromJson(res);
                if (map == null) throw new RuntimeException("P6eHuoMaoChannelInventory web socket url null");
                wsUrl = String.valueOf(map.get("host"));
                wssUrl = String.valueOf(map.get("host_wss"));
            }
        } catch (Exception e) {
            throw new RuntimeException("P6eHuoMaoChannelInventory web socket url null");
        }
    }

    private String getId(String urlPath) {
        String res = P6eHttpCommon.doGet(urlPath);
        if (res == null) return null;
        StringBuilder id = new StringBuilder();
        String[] resArray = res.split("current_id");
        if (resArray.length >= 2) {
            for (int i = 2; i < resArray[1].length(); i++) {
                int t = (int) resArray[1].charAt(i);
                if (t >= 48 && t <= 57) id.append((char) t);
                else return id.toString();
            }
        }
        return null;
    }

    P6eHuoMaoChannelInventory(int id) {
        this.id = String.valueOf(id);
    }

    P6eHuoMaoChannelInventory(String urlPath) {
        this.id = getId(urlPath);
        if (this.id == null) throw new RuntimeException("P6eHuoMaoChannelInventory get id null");
    }

    String getWebSocketWsUrl() {
        if (wsUrl == null || wssUrl == null) webSocketUrl();
        return wsUrl;
    }

    String getWebSocketWssUrl() {
        if (wsUrl == null || wssUrl == null) webSocketUrl();
        return wssUrl;
    }

    String getLoginInfo() {
        return P6eToolCommon.translate(LOGIN_MESSAGE, "id", id);
    }

    int getLoginType() {
        return LOGIN_TYPE_MESSAGE;
    }

    String getPantInfo() {
        return PANT_MESSAGE;
    }

    int getPantType() {
        return PANT_TYPE_MESSAGE;
    }

}
