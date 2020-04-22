package com.p6e.broadcast.common;

import com.p6e.broadcast.channel.P6eChannelTimeCallback;
import org.junit.Test;

public class P6eTimeCallbackTest {

    @Test
    public void test() throws InterruptedException {
        P6eChannelTimeCallback.create();
        P6eChannelTimeCallback.addConfig(new P6eChannelTimeCallback.Config(3, System.out::println));
        P6eChannelTimeCallback.addConfig(new P6eChannelTimeCallback.Config(3, true, System.out::println));
        P6eChannelTimeCallback.addConfig(new P6eChannelTimeCallback.Config(3, true, true, System.out::println));
        Thread.sleep(Integer.MAX_VALUE);
    }

}
