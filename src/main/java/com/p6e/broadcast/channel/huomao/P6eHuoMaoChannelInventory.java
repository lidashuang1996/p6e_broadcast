package com.p6e.broadcast.channel.huomao;

import com.p6e.broadcast.common.P6eHttpCommon;
import com.p6e.broadcast.common.P6eToolCommon;

import java.util.Map;

/**
 * 火猫直播 http://www.huomao.com/
 * 火猫直播清单处理类
 * 1. 获取房间 ID
 * 2. 获取登录房间消息
 * 3. 获取心跳房间消息
 * 4. 获取 WS 连接的地址
 * 5. 获取 WSS 连接的地址
 * 6. 获取登录房间消息类型
 * 7. 获取心跳房间消息类型
 * @version 1.0
 */
class P6eHuoMaoChannelInventory {

    // 官网的 URL 地址
    private static final String URL = "https://www.huomao.com";
    // 获取 WebSocket 的 URL 地址
    private static final String WEB_SOCKET_URL = "http://www.huomao.com/ajax/goimConf?type=h5";
    // 房间登录的消息内容
    private static final String LOGIN_MESSAGE = "{\"Uid\":0, \"Rid\":${id}}";
    // 房间心跳消息的内容
    private static final String PANT_MESSAGE = "";
    // 发送消息的类型
    private static final int SEND_TYPE_MESSAGE = 1;
    // 房间登录的消息类型
    private static final int LOGIN_TYPE_MESSAGE = 7;
    // 房间登录成功返回的消息类型
    private static final int LOGIN_RESULT_TYPE_MESSAGE = 8;
    // 房间心跳的消息类型
    private static final int PANT_TYPE_MESSAGE = 2;

    // WebSocket 的 WS 协议的地址
    private String wsUrl;
    // WebSocket 的 WSS 协议的地址
    private String wssUrl;
    // 房间的 ID
    private String id;

    /**
     * 获取 Web Socket 连接地址的方法
     */
    private void webSocketUrl() {
        try {
            String res = P6eHttpCommon.doGet(WEB_SOCKET_URL);
            if (res != null && !"".equals(res)) {
                Map map = P6eToolCommon.fromJson(res);
                if (map == null) throw new RuntimeException("P6eHuoMaoChannelInventory web socket url null");
                wsUrl = String.valueOf(map.get("host"));
                wssUrl = String.valueOf(map.get("host_wss"));
            }
        } catch (Exception e) {
            throw new RuntimeException("P6eHuoMaoChannelInventory web socket url null");
        }
    }

    /**
     * 通过页面的 URL 获取房间 ID 的方法
     * @param urlPath 页面的 URL
     * @return 房间 ID
     */
    private String getId(String urlPath) {
        if (!urlPath.startsWith(URL))
            throw new RuntimeException("Request address is not https://www.huomao.com ");
        String res = P6eHttpCommon.doGet(urlPath);
        if (res == null) throw new RuntimeException("Request address is not https://www.huomao.com ");
        StringBuilder id = new StringBuilder();
        String[] resArray = res.split("current_id");
        if (resArray.length >= 2) {
            for (int i = 2; i < resArray[1].length(); i++) {
                int t = (int) resArray[1].charAt(i);
                if (t >= 48 && t <= 57) id.append((char) t);
                else return id.toString();
            }
        }
        throw new RuntimeException("Request address is not https://www.huomao.com ");
    }

    /**
     * 火猫直播清单的构造方法
     * 直播房间的 ID ==>
     *      浏览器省察页面里面的 current_uid 是房间的 ID
     *      [ https://www.huomao.com/717 ==> current_uid:'38034' ]
     * @param id 直播房间的 ID
     */
    P6eHuoMaoChannelInventory(int id) {
        this.id = String.valueOf(id);
    }

    /**
     * 火猫直播清单的构造方法
     * 直播房间的 URL 地址 ==>
     *      https://www.huomao.com/717 [ 火猫直播的房间地址 ]
     * @param urlPath 直播房间的 URL 地址
     */
    P6eHuoMaoChannelInventory(String urlPath) {
        this.id = getId(urlPath);
    }

    /**
     * 获取火猫房间的 ID
     * @return 火猫房间的 ID
     */
    String getId() {
        return this.id;
    }

    /**
     * 获取 WebSocket 的 WS Url 地址
     * @return WS Url 地址
     */
    String getWebSocketWsUrl() {
        if (wsUrl == null || wssUrl == null) webSocketUrl();
        return wsUrl;
    }

    /**
     * 获取 WebSocket 的 WSS Url 地址
     * @return WSS Url 地址
     */
    String getWebSocketWssUrl() {
        if (wsUrl == null || wssUrl == null) webSocketUrl();
        return wssUrl;
    }

    /**
     * 获取登录消息内容
     * @return 登录消息内容
     */
    String getLoginInfo() {
        return P6eToolCommon.translate(LOGIN_MESSAGE, "id", id);
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

    /**
     * 获取心跳消息内容
     * @return 心跳消息内容
     */
    String getPantInfo() {
        return PANT_MESSAGE;
    }

    /**
     * 获取心跳消息的类型
     * @return 心跳消息类型
     */
    int getPantType() {
        return PANT_TYPE_MESSAGE;
    }

    /**
     * 获取发送消息的类型
     * @return 发送消息的类型
     */
    int getSendType() {
        return SEND_TYPE_MESSAGE;
    }

}
