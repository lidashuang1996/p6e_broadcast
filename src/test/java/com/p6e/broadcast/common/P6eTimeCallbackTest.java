package com.p6e.broadcast.common;

import com.p6e.broadcast.channel.P6eChannelTimeCallback;
import org.junit.Test;

public class P6eTimeCallbackTest {

    @Test
    public void test() throws InterruptedException {
        P6eChannelTimeCallback.create();
        P6eChannelTimeCallback.Config config = new P6eChannelTimeCallback.Config(3, System.out::println);
        P6eChannelTimeCallback.addConfig(config);
//        P6eChannelTimeCallback.addConfig(new P6eChannelTimeCallback.Config(3, true, System.out::println));
//        P6eChannelTimeCallback.addConfig(new P6eChannelTimeCallback.Config(3, true, true, System.out::println));
        Thread.sleep(10000);
        P6eChannelTimeCallback.removeConfig(config);
        Thread.sleep(5000);
        P6eChannelTimeCallback.destroy();
        Thread.sleep(10000);
    }

}
