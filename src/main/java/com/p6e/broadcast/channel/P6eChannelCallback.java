package com.p6e.broadcast.channel;


import com.p6e.broadcast.channel.douyu.P6eDouYuChannelMessage;

import java.util.List;

public interface P6eChannelCallback {

    public interface DouYu {
        public void execute(List<P6eDouYuChannelMessage> messages);
    }

}
