package com.p6e.broadcast.channel.huomao;

import com.p6e.broadcast.channel.P6eChannelCallback;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 火猫直播 http://www.huomao.com/
 *
 * 获取火猫直播房间信息的测试类
 * 1.
 *
 * @version 1.0
 */
public class HuoMaoTest {

    private Logger logger = LoggerFactory.getLogger(HuoMaoTest.class);

    @Test
    public void channel() {
        P6eHuoMaoChannel.create("http://www.huomao.com/880277", new P6eChannelCallback.HuoMao() {
            @Override
            public void execute(List<P6eHuoMaoChannelMessage> messages) {
                for (P6eHuoMaoChannelMessage message : messages) {
                    System.out.println(message.__text__());
                }
            }
        });

        P6eHuoMaoChannel.create(13344, new P6eChannelCallback.HuoMao() {
            @Override
            public void execute(List<P6eHuoMaoChannelMessage> messages) {
                for (P6eHuoMaoChannelMessage message : messages) {
                    System.out.println(message.__text__());
                }
            }
        });
    }

}
