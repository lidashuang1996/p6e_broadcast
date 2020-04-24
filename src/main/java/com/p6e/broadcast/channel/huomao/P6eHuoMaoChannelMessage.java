package com.p6e.broadcast.channel.huomao;

import com.p6e.broadcast.common.P6eToolCommon;

import java.util.Map;

/**
 * 火猫直播 http://www.huomao.com/
 * 火猫直播消息模型类
 * @version 1.0
 */
public class P6eHuoMaoChannelMessage {
    /** 原消息的内容 */
    private byte[] __byte__;
    /** 原消息的文本内容 */
    private String __text__;
    /** 原消息的的类型 */
    private int __type__;
    /** 反序列化后的消息内容 */
    private Object data;

    /**
     * 创建火猫消息模型类
     * @param bytes 消息二进制流数组
     * @param type 消息类型
     * @param text 消息文本内容
     * @return P6eHuoMaoChannelMessage 消息模型类
     */
    static P6eHuoMaoChannelMessage build(byte[] bytes, int type, String text) {
        return new P6eHuoMaoChannelMessage(bytes, type, text);
    }

    /**
     * 构造方法消息模型类
     * @param bytes 消息二进制数组
     * @param type 消息类型
     * @param text 消息文本内容
     */
    private P6eHuoMaoChannelMessage(byte[] bytes, int type, String text) {
        this.__byte__ = bytes;
        this.__text__ = text;
        this.__type__ = type;
        /* 消息类型为 5 的，才是需要序列化的内容 */
        if (this.__type__ == 5) this.data = deserialization(text);
        else this.data = text;
    }

    /**
     * 获取消息二进制数组
     * @return 消息二进制数组
     */
    public byte[] __byte__() {
        return __byte__;
    }

    /**
     * 获取消息类型
     * @return 消息类型
     */
    public String __text__() {
        return __text__;
    }

    /**
     * 获取消息类型
     * @return 消息类型
     */
    public int ___type__() {
        return __type__;
    }

    /**
     * 获取消息反序列化的对象
     * @return 反序列化的对象
     */
    public Object data() {
        return data;
    }

    /**
     * 对消息进行序列化处理
     * @param text 消息的内容
     * @return 序列化后的内容
     */
    private static Map deserialization(String text) {
        return P6eToolCommon.fromJson(text);
    }
}
