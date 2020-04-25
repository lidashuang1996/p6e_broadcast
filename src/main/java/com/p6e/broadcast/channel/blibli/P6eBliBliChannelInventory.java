package com.p6e.broadcast.channel.blibli;

import com.p6e.broadcast.common.P6eHttpCommon;
import com.p6e.broadcast.common.P6eToolCommon;

import java.util.List;
import java.util.Map;

/**
 * BliBli 直播 https://live.bilibili.com/
 * BliBli 直播清单处理类
 * 1. 获取房间认证 TOKEN
 * 2. 获取登录房间消息
 * 3. 获取心跳房间消息
 * 4. 获取 WS 连接的地址
 * 5. 获取 WSS 连接的地址
 * 6. 获取登录房间消息类型
 * 7. 获取登录房间返回消息类型
 * 8. 获取心跳房间消息类型
 * @version 1.0
 */
class P6eBliBliChannelInventory {

    // 获取房间 Web Socket 连接地址的 URL
    private static final String WEB_SOCKET_URL =
            "https://api.live.bilibili.com/room/v1/Danmu/getConf?room_id=${room}&platform=pc&player=web";
    // 房间心跳的消息内容
    private static final String LOGIN_MESSAGE = "{\"uid\":0,\"roomid\":${room},\"protover\":2," +
            "\"platform\":\"web\",\"clientver\":\"1.10.3\",\"type\":2,\"key\":\"${token}\"}";
    // 房间心跳的消息内容
    private static final String PANT_MESSAGE = "[object Object]";
    // 房间心跳的消息类型
    private static final int PANT_TYPE_MESSAGE = 2;
    // 房间登录的消息类型
    private static final int LOGIN_TYPE_MESSAGE = 7;
    // 房间登录成功返回的消息类型
    private static final int LOGIN_RESULT_TYPE_MESSAGE = 8;

    // 房间的 ID
    private String rid;
    // WebSocket 的 WS 协议的地址
    private String wsUrl;
    // WebSocket 的 WSS 协议的地址
    private String wssUrl;
    // 房间的 TOKEN
    private String token;

    /**
     * BliBli 直播清单的构造方法
     * 直播房间的 ID ==> 房间的编号
     * @param rid 直播房间的 ID
     */
    P6eBliBliChannelInventory(String rid) {
        this.rid = rid;
    }

    /**
     * 获取 Web Socket 连接地址的方法
     */
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

    /**
     * 获取 WebSocket 的 WS Url 地址
     * @return WS Url 地址
     */
    String getWebSocketWsUrl() {
        if (wsUrl == null || wssUrl == null || token == null) webSocketUrl();
        return wsUrl;
    }

    /**
     * 获取 WebSocket 的 WSS Url 地址
     * @return WSS Url 地址
     */
    String getWebSocketWssUrl() {
        if (wsUrl == null || wssUrl == null || token == null) webSocketUrl();
        return wssUrl;
    }

    /**
     * 获取 WebSocket 的认证 TOKEN
     * @return TOKEN
     */
    String getWebSocketToken() {
        if (wsUrl == null || wssUrl == null || token == null) webSocketUrl();
        return token;
    }

    /**
     * 获取心跳消息内容
     * @return 心跳消息内容
     */
    String getPantInfo() {
        return PANT_MESSAGE;
    }

    /**
     * 获取登录消息内容
     * @return 登录消息内容
     */
    String getLoginInfo() {
        return P6eToolCommon.translate(LOGIN_MESSAGE, "room", rid, "token", this.getWebSocketToken());
    }

    /**
     * 获取心跳消息的类型
     * @return 心跳消息类型
     */
    int getPantType() {
        return PANT_TYPE_MESSAGE;
    }

    /**
     * 获取登录消息类型
     * @return 登录消息类型
     */
    int getLoginType() {
        return LOGIN_TYPE_MESSAGE;
    }

    /**
     * 获取登录返回的消息类型
     * @return 登录返回的消息类型
     */
    int getLoginResultType() {
        return LOGIN_RESULT_TYPE_MESSAGE;
    }

}
