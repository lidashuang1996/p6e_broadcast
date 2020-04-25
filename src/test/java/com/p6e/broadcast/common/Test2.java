package com.p6e.broadcast.common;

import com.p6e.broadcast.channel.P6eChannelCallback;
import com.p6e.broadcast.channel.douyu.P6eDouYuChannel;
import com.p6e.broadcast.channel.douyu.P6eDouYuChannelMessage;
import org.junit.Test;

import java.util.List;

public class Test2 {
    @Test
    public void ccc () throws InterruptedException {
        P6eDouYuChannel.create("105025", new P6eChannelCallback.DouYu() {
            @Override
            public void execute(List<P6eDouYuChannelMessage> message) {

            }
        });
        Thread.sleep(Integer.MAX_VALUE);
    }
}
