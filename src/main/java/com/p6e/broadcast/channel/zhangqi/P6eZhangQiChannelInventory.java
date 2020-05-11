package com.p6e.broadcast.channel.zhangqi;

import com.p6e.broadcast.common.P6eHttpCommon;
import com.p6e.broadcast.common.P6eToolCommon;

import java.util.Map;

class P6eZhangQiChannelInventory {

    private static final String WEB_SOCKET_URL = "wss://gw.zhanqi.tv/";
    // 官网的 URL 地址
    private static final String HTTPS_URL = "https://www.zhanqi.tv";
    private static final String HTTP_URL = "http://www.zhanqi.tv";

    private String rid;
    private String urlPath;
    P6eZhangQiChannelInventory(String urlPath) {
        this.rid = getId(urlPath);
    }


    /**
     * 通过页面的 URL 获取房间 ID 的方法
     * @param urlPath 页面的 URL
     * @return 房间 ID
     */
    private String getId(String urlPath) {
        if (!(urlPath.startsWith(HTTP_URL) || urlPath.startsWith(HTTPS_URL)))
            throw new RuntimeException("Request address is not https://www.zhanqi.tv or http://www.zhanqi.tv ");
        String res = P6eHttpCommon.doGet(urlPath);
        if (res == null) throw new RuntimeException("Request address is not https://www.zhanqi.tv ");
        StringBuilder id = new StringBuilder();
        String[] resArray = res.split(",\"RoomId\":");
        if (resArray.length >= 2) {
            for (int i = 0; i < resArray[1].length(); i++) {
                int t = (int) resArray[1].charAt(i);
                if (t >= 48 && t <= 57) id.append((char) t);
                else return id.toString();
            }
        }
        throw new RuntimeException("Request address is not https://www.zhanqi.tv ");
    }

    /**
     * 获取 WebSocket URL 地址
     * @return URL 地址
     */
    String getWebSocketUrl() {
        return WEB_SOCKET_URL;
    }

    /**
     * 获取房间的 RID
     * @return RID 数据
     */
    String getRid() {
        return this.rid;
    }

    String getSvrisokreq() {
        return "{\"cmdid\":\"svrisokreq\"}";
    }

    byte[] getSvrisokreqBytes() {
        return P6eToolCommon.hexToByte("0101e803");
    }

    String getLoginreq() {
        try {
            String res = P6eHttpCommon.doGet("https://www.zhanqi.tv/api/public/room.viewer");
            Map<String, Object> map = P6eToolCommon.fromJson(res);
            if (map == null) throw new RuntimeException();
            Map mapData = (Map) map.get("data");
            if (mapData == null) throw new RuntimeException();
            long uid = (long) mapData.get("uid");
            long gid = (long) mapData.get("gid");
            String sid = (String) mapData.get("sid");
            long timestamp = (long) mapData.get("timestamp");
            return "{\"roomid\":" + rid + ",\"uid\":" + uid + ",\"gid\":" + gid + ",\"sid\":\""
                    + sid + "\",\"timestamp\":" + timestamp + ",\"t\":0,\"thirdaccount\":\"\",\"fx\":0," +
                    "\"hideslv\":0,\"tagid\":0,\"ajp\":0,\"cmdid\":\"loginreq\",\"fromjs\":1,\"fhost\":\"h5\"}";
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    byte[] getLoginreqBytes() {
        return P6eToolCommon.hexToByte("0c0f1027");
    }

    String getPant() {
        return "";
    }

    byte[] getPantBytes() {
        return P6eToolCommon.hexToByte("0a015927");
    }

}
