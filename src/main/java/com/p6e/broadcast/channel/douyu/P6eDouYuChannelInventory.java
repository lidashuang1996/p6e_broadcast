package com.p6e.broadcast.channel.douyu;

import com.p6e.broadcast.common.P6eToolCommon;

public class P6eDouYuChannelInventory {

    private static final String WEB_SOCKET_URL = "wss://danmuproxy.douyu.com:8504/";
    private static final String LOGIN_MESSAGE = "type@=loginreq/roomid@=${room}/";
    private static final String GROUP_MESSAGE = "type@=joingroup/rid@=${room}/gid@=0/";
    private static final String ALL_GIFT_MESSAGE = "type@=dmfbdreq/dfl@=sn@AA=105@ASss@AA=0@AS@Ssn" +
            "@AA=106@ASss@AA=0@AS@Ssn@AA=107@ASss@AA=0@AS@Ssn@AA=108@ASss@AA=0@AS@Ssn@AA=110@ASss@AA=0@AS@S/";
    private static final String PANT_MESSAGE = "type@=mrkl/";
    private static final int CLIENT_TYPE_MESSAGE = 689;
    private static final int SERVICE_TYPE_MESSAGE = 690;

    private String rid;

    public P6eDouYuChannelInventory(String rid) {
        this.rid = rid;
    }

    public String getWebSocketUrl() {
        return WEB_SOCKET_URL;
    }

    public String getLoginInfo() {
        return P6eToolCommon.translate(LOGIN_MESSAGE, "room", rid);
    }

    public String getGroupInfo() {
        return P6eToolCommon.translate(GROUP_MESSAGE, "room", rid);
    }

    public String getAllGiftInfo() {
        return ALL_GIFT_MESSAGE;
    }

    public String getPant() {
        return PANT_MESSAGE;
    }

    public int getClientMessageType() {
        return CLIENT_TYPE_MESSAGE;
    }

    public int getServiceMessageType() {
        return SERVICE_TYPE_MESSAGE;
    }

}
