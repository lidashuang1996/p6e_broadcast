package com.p6e.broadcast.channel.blibli;

import com.p6e.broadcast.common.P6eToolCommon;

import java.util.List;
import java.util.Map;

public class P6eBliBliChannelMessage {

    private byte[] __byte__;
    private String __text__;
    private int __type__;
    private Map data;


    public static P6eBliBliChannelMessage build(byte[] bytes, int type, String text) {
        return new P6eBliBliChannelMessage(bytes, type, text);
    }

    private P6eBliBliChannelMessage(byte[] bytes, int type, String text) {
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
