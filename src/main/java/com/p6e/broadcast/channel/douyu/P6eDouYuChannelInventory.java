package com.p6e.broadcast.channel.douyu;

import com.p6e.broadcast.common.P6eToolCommon;

/**
 * 斗鱼直播 https://www.douyu.com
 * 斗鱼直播清单处理类
 * 1. 获取房间 ID
 * 2. 获取登录房间消息
 * 3. 获取心跳房间消息
 * 4. 获取 WS 连接的地址
 * 5. 获取 WSS 连接的地址
 * 6. 获取登录房间消息类型
 * 7. 获取心跳房间消息类型
 * @version 1.0
 */
class P6eDouYuChannelInventory {

    // Web Socket URL 地址
    private static final String WEB_SOCKET_URL = "wss://danmuproxy.douyu.com:8504/";
    // 登录的消息模板
    private static final String LOGIN_MESSAGE = "type@=loginreq/roomid@=${room}/";
    // 加入组消息模板
    // private static final String GROUP_MESSAGE = "type@=joingroup/rid@=${room}/gid@=0/";
    private static final String GROUP_MESSAGE = "type@=joingroup/rid@=${room}/gid@=-9999/";
    // 接收全部礼物的消息
    private static final String ALL_GIFT_MESSAGE = "type@=dmfbdreq/dfl@=sn@AA=105@ASss@AA=0@AS@Ssn" +
            "@AA=106@ASss@AA=0@AS@Ssn@AA=107@ASss@AA=0@AS@Ssn@AA=108@ASss@AA=0@AS@Ssn@AA=110@ASss@AA=0@AS@S/";
    // 心跳消息内容
    private static final String PANT_MESSAGE = "type@=mrkl/";
    // 客户端发送消息的类型
    private static final int CLIENT_TYPE_MESSAGE = 689;
    // 服务端发送消息的类型
    private static final int SERVICE_TYPE_MESSAGE = 690;
    // 房间的 ID
    private String rid;

    /**
     * 斗鱼直播清单的构造方法
     * 直播房间的 ID [ 斗鱼直播的房间地址 ]
     * @param rid 直播房间的 ID 地址
     */
    P6eDouYuChannelInventory(String rid) {
        this.rid = rid;
    }

    /**
     * 获取 Web Socket Url 地址
     * @return URL 地址
     */
    String getWebSocketUrl() {
        return WEB_SOCKET_URL;
    }

    /**
     * 获取登录的信息
     * @return 登录信息
     */
    String getLoginInfo() {
        return P6eToolCommon.translate(LOGIN_MESSAGE, "room", rid);
    }

    /**
     * 获取组信息
     * @return 组信息
     */
    String getGroupInfo() {
        return P6eToolCommon.translate(GROUP_MESSAGE, "room", rid);
    }

    /**
     * 获取全部礼物的数据
     * @return 全部礼物的数据
     */
    String getAllGiftInfo() {
        return ALL_GIFT_MESSAGE;
    }

    /**
     * 获取服务端消息类型
     * @return 服务端消息类型
     */
    String getPant() {
        return PANT_MESSAGE;
    }

    /**
     * 获取客户端消息类型
     * @return 客户端消息类型
     */
    int getClientMessageType() {
        return CLIENT_TYPE_MESSAGE;
    }

    int getServiceMessageType() {
        return SERVICE_TYPE_MESSAGE;
    }

}
