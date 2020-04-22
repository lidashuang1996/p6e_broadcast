package com.p6e.broadcast.channel.douyu;

import java.util.*;

public class P6eDouYuChannelMessage {

    private byte[] __byte__;
    private String __text__;
    private Map data;


    public static P6eDouYuChannelMessage build(byte[] bytes, String text) {
        return new P6eDouYuChannelMessage(bytes, text);
    }

    private P6eDouYuChannelMessage(byte[] bytes, String text) {
        this.__byte__ = bytes;
        this.__text__ = text;
        this.data = (Map) deserialization(text);
    }

    public byte[] __byte__() {
        return __byte__;
    }

    public String __text__() {
        return __text__;
    }

    public Map data() {
        return data;
    }

    private static Object deserialization(String text) {
        boolean bool = false;
        List<Object> list = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        String[] ts = text.trim().split("/");
        for (String t : ts) {
            String[] is = t.split("@=");
            if (is.length == 1) {
                if (!(is[0].contains("/") || is[0].contains("@S") || is[0].contains("@A"))) map.put(is[0], "");
                else {
                    bool = true;
                    is[0] = is[0].replaceAll("@S", "\\/").replaceAll("@A", "@");
                    list.add(deserialization(is[0]));
                }
            } else {
                is[1] = is[1].replaceAll("@S", "\\/").replaceAll("@A", "@");
                if (is[1].contains("@=") || is[1].contains("@A") || is[1].contains("@S")) map.put(is[0], deserialization(is[1]));
                else map.put(is[0], is[1]);
            }
        }
        return bool ? list : map;
    }
}
