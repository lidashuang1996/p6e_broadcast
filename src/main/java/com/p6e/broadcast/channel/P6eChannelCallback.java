package com.p6e.broadcast.channel;


import com.p6e.broadcast.channel.blibli.P6eBliBliChannelMessage;
import com.p6e.broadcast.channel.douyu.P6eDouYuChannelMessage;
import com.p6e.broadcast.channel.huomao.P6eHuoMaoChannelMessage;

import java.util.List;

public interface P6eChannelCallback {

    public interface DouYu {
        public void execute(List<P6eDouYuChannelMessage> messages);
    }

    public interface BliBli {
        public void execute(List<P6eBliBliChannelMessage> messages);
    }

    public interface HuoMao {
        public void execute(List<P6eHuoMaoChannelMessage> messages);
    }

}
