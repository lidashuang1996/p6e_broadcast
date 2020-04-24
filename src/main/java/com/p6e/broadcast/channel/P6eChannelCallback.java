package com.p6e.broadcast.channel;


import com.p6e.broadcast.channel.blibli.P6eBliBliChannelMessage;
import com.p6e.broadcast.channel.douyu.P6eDouYuChannelMessage;
import com.p6e.broadcast.channel.huomao.P6eHuoMaoChannelMessage;

import java.util.List;

/**
 * 弹幕服务连接器的消息回调类
 * @version 1.0
 */
public interface P6eChannelCallback {

    /**
     * 斗鱼直播的消息回调
     */
    public interface DouYu {
        public void execute(List<P6eDouYuChannelMessage> messages);
    }

    /**
     * BliBli直播的消息回调
     */
    public interface BliBli {
        public void execute(List<P6eBliBliChannelMessage> messages);
    }

    /**
     * 火猫直播的消息回调
     */
    public interface HuoMao {
        public void execute(List<P6eHuoMaoChannelMessage> messages);
    }

}
