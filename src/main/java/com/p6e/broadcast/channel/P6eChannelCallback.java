package com.p6e.broadcast.channel;


import com.p6e.broadcast.channel.blibli.P6eBliBliChannelMessage;
import com.p6e.broadcast.channel.douyu.P6eDouYuChannelMessage;
import com.p6e.broadcast.channel.huomao.P6eHuoMaoChannelMessage;
import com.p6e.broadcast.channel.kuaishou.P6eKuaiShouChannelMessage;
import com.p6e.broadcast.channel.lognzhu.P6eLongZhuChannelMessage;
import com.p6e.broadcast.channel.zhangqi.P6eZhangQiChannelMessage;

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

    /**
     * 龙珠直播的消息回调
     */
    public interface LongZhu {
        public void execute(List<P6eLongZhuChannelMessage> messages);
    }

    /**
     * 快手直播的消息回调
     */
    public interface KuaiShou {
        public void execute(List<P6eKuaiShouChannelMessage> messages);
    }


    public interface ZhangQi {
        public void execute(List<P6eZhangQiChannelMessage> messages);
    }
}
