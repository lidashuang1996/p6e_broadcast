package com.p6e.broadcast.channel.huomao;

import com.p6e.broadcast.common.P6eToolCommon;

import java.util.Map;

public class P6eHuoMaoChannelMessage {
    private byte[] __byte__;
    private String __text__;
    private int __type__;
    private Map data;


    static P6eHuoMaoChannelMessage build(byte[] bytes, int type, String text) {
        return new P6eHuoMaoChannelMessage(bytes, type, text);
    }

    private P6eHuoMaoChannelMessage(byte[] bytes, int type, String text) {
        this.__byte__ = bytes;
        this.__text__ = text;
        this.__type__ = type;
        this.data = deserialization(text);
    }

    public byte[] __byte__() {
        return __byte__;
    }

    public String __text__() {
        return __text__;
    }

    public int ___type__() {
        return __type__;
    }

    public Map data() {
        return data;
    }

    private static Map deserialization(String text) {
        return P6eToolCommon.fromJson(text);
    }
}
