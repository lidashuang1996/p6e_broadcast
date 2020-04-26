package com.p6e.broadcast.channel.lognzhu;

import com.p6e.broadcast.common.P6eToolCommon;

import java.util.Map;

/**
 * 龙珠直播 http://www.longzhu.com/
 * 龙珠直播消息模型类
 * @version 1.0
 */
public class P6eLongZhuChannelMessage {
    private String appId;
    private String rid;
    private String fromIdentification;
    private String toIdentification;
    private String msgId;
    private Long timestamp;
    private Long qos;
    private String traceId;
    private Map<String, Object> msg;

    /**
     * 创建火猫消息模型类
     * @param text 消息文本内容
     * @return P6eLongZhuChannelMessage 消息模型类
     */
    static P6eLongZhuChannelMessage build(String text) {
        return P6eToolCommon.fromJson(text, P6eLongZhuChannelMessage.class);
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getRid() {
        return rid;
    }

    public void setRid(String rid) {
        this.rid = rid;
    }

    public String getFromIdentification() {
        return fromIdentification;
    }

    public void setFromIdentification(String fromIdentification) {
        this.fromIdentification = fromIdentification;
    }

    public String getToIdentification() {
        return toIdentification;
    }

    public void setToIdentification(String toIdentification) {
        this.toIdentification = toIdentification;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Long getQos() {
        return qos;
    }

    public void setQos(Long qos) {
        this.qos = qos;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public Map<String, Object> getMsg() {
        return msg;
    }

    public void setMsg(Map<String, Object> msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "{" + "\"appId\":\"" +
                appId + '\"' +
                ",\"rid\":\"" +
                rid + '\"' +
                ",\"fromIdentification\":\"" +
                fromIdentification + '\"' +
                ",\"toIdentification\":\"" +
                toIdentification + '\"' +
                ",\"msgId\":\"" +
                msgId + '\"' +
                ",\"timestamp\":" +
                timestamp +
                ",\"qos\":" +
                qos +
                ",\"traceId\":\"" +
                traceId + '\"' +
                ",\"msg\":" +
                msg +
                '}';
    }
}
