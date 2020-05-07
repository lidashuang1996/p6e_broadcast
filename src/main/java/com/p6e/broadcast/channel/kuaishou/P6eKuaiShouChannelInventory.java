package com.p6e.broadcast.channel.kuaishou;

import com.p6e.broadcast.common.P6eHttpCommon;
import com.p6e.broadcast.common.P6eToolCommon;

import java.util.HashMap;
import java.util.Map;

/**
 * 快手直播 https://live.kuaishou.com/
 * 快手直播清单处理类
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
class P6eKuaiShouChannelInventory {

    // 官网的 URL 地址
    private static final String HTTPS_URL = "https://live.kuaishou.com";
    private static final String HTTP_URL = "http://live.kuaishou.com";

    // 获取房间信息的 URL
    private static final String KUAI_SHOU_API_URL = "https://live.kuaishou.com/m_graphql";

    private String name;


    P6eKuaiShouChannelInventory(String urlPath) {

    }

    /**
     * 获取房间的直播流名称
     * @return 执行获取请求的结果
     */
    private static String obtainRoom() {
        Map<String, Object> param = new HashMap<>();
        Map<String, String> variables = new HashMap<>();
        variables.put("campaignId", "30");
        param.put("operationName", "ActivityTemplateInfo");
        param.put("variables", variables);
        param.put("query", "ActivityTemplateInfo($campaignId: String) {  webActivityTemplateTableData(campaignId: $campaignId) {    data    __typename  }  webActivityTemplateConfig(campaignId: $campaignId) {    panelPicUrl    roundPicUrl    roundActivePicUrl    onlyFirstRound    webPicUrl    title    __typename  }  webActivityTemplateLiveData(campaignId: $campaignId) {    userId    buttonText    __typename  }  webActivityTemplateScheduleData(campaignId: $campaignId) {    id    name    isFake    matches {      id      name      groupA {        id        name        picUrl        __typename      }      groupB {        id        name        picUrl        __typename      }      groupAScore      groupBScore      status      startTime      endTime      buttonLink      buttonText      __typename    }    __typename  }}");
        return P6eHttpCommon.doPost(KUAI_SHOU_API_URL, P6eToolCommon.toJson(param));
    }

    public static void main(String[] args) {
        System.out.println(obtainRoom());
    }

    /**
     * 通过页面的 URL 获取房间 name 的方法
     * @param urlPath 页面的 URL
     * @return 房间 name
     */
    private String getName(String urlPath) {
        if (!(urlPath.startsWith(HTTP_URL) || urlPath.startsWith(HTTPS_URL)))
            throw new RuntimeException("Request address is not https://live.kuaishou.com or http://live.kuaishou.com ");
        String res = obtainRoom();
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


    String getId() {
        return null;
    }

    String getWebSocketWssUrl() {
        return null;
    }
}
