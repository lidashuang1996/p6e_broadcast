package com.p6e.broadcast.channel.lognzhu;

import com.p6e.broadcast.common.P6eHttpCommon;
import com.p6e.broadcast.common.P6eToolCommon;

import java.util.Map;

/**
 * 龙珠直播 http://www.longzhu.com/
 * 龙珠直播清单处理类
 * 1. 获取房间 ID
 * 2. 获取登录房间消息
 * 3. 获取心跳房间消息
 * 4. 获取 WS 连接的地址
 * 5. 获取 WSS 连接的地址
 * 6. 获取登录房间消息类型
 * 7. 获取登录房间返回消息类型
 * 8. 获取心跳房间消息类型
 * @version 1.0
 */
class P6eLongZhuChannelInventory {

    // 官网的 URL 地址
    private static final String HTTPS_URL = "https://star.longzhu.com";
    private static final String HTTP_URL = "http://star.longzhu.com";
    // 房间心跳消息的内容
    private static final String PANT_MESSAGE = "{\"body\":\"{}\",\"op\":2,\"sid\":0,\"seq\":0}";
    // MID 消息的内容
    private static final String MID_MESSAGE = "{\"body\":{\"msgId\":\"${mid}\"},\"op\":6,\"sid\":0,\"seq\":0}";
    // 获取 WebSocket 的 URL 地址
    private static final String GET_WEB_SOCKET_URL = "http://api.longzhu.com/" +
            "v1/gateway/func/checkroom?roomid=${id}&wangsu=1&lzv=1";
    private static final String WEB_SOCKET_URL = "ws://ws.longzhu.com:8805/?" +
            "appId=${app}&roomId=${room}&msgVersion=1.0&token=${token}";
    // WebSocket 的 WS 协议的地址
    private String wsUrl;
    // 房间的 ID
    private String id;


    /**
     * 获取 Web Socket 连接地址的方法
     */
    private synchronized void webSocketUrl() {
        try {
            String resInfo = P6eHttpCommon.doGet(P6eToolCommon.translate(GET_WEB_SOCKET_URL, "id", id));
            Map data = P6eToolCommon.fromJson(resInfo);
            if (data != null && "success".equals(data.get("message")) && data.get("data") != null) {
                String appId = ((Map) data.get("data")).get("appId").toString();
                String token = ((Map) data.get("data")).get("zhongtaiToken").toString();
                wsUrl = P6eToolCommon.translate(WEB_SOCKET_URL, "app", appId, "room", id, "token", token);
            } else throw new RuntimeException("P6eHuoMaoChannelInventory web socket url null");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("P6eHuoMaoChannelInventory web socket url null");
        }
    }

    /**
     * 通过页面的 URL 获取房间 ID 的方法
     * @param urlPath 页面的 URL
     * @return 房间 ID
     */
    private String getId(String urlPath) {
        if (!(urlPath.startsWith(HTTP_URL) || urlPath.startsWith(HTTPS_URL)))
            throw new RuntimeException("Request address is not https://star.longzhu.com or http://star.longzhu.com ");
        String res = P6eHttpCommon.doGet(P6eToolCommon.translate(urlPath, "id", id));
        if (res == null) throw new RuntimeException("Request address is not https://www.huomao.com ");
        StringBuilder rid = new StringBuilder();
        String[] rs = res.split("streaming");
        if (rs.length >= 2) {
            for (int i = 4; i < rs[1].length(); i++) {
                int t = (int) rs[1].charAt(i);
                if (t >= 48 && t <= 57) rid.append((char) t);
                else return rid.toString();
            }
        }
        throw new RuntimeException("Request address is not https://www.huomao.com ");
    }

    /**
     * 龙珠直播清单的构造方法
     * 直播房间的 ID ==>
     *      浏览器省察页面里面的 http://longzhu.com/streaming?id=*** 是房间的 ID
     * @param id 直播房间的 ID
     */
    P6eLongZhuChannelInventory(int id) {
        this.id = String.valueOf(id);
    }

    /**
     * 龙珠直播清单的构造方法 [ http://star.longzhu.com/*** ]
     * @param urlPath 直播房间的 URL 地址
     */
    P6eLongZhuChannelInventory(String urlPath) {
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
        if (wsUrl == null) webSocketUrl();
        return wsUrl;
    }

    /**
     * 获取登录消息内容
     * @return 登录消息内容
     */
    String getMidInfo(String mid) {
        return P6eToolCommon.translate(MID_MESSAGE, "mid", mid);
    }

    /**
     * 获取心跳消息内容
     * @return 心跳消息内容
     */
    String getPantInfo() {
        return PANT_MESSAGE;
    }

}
