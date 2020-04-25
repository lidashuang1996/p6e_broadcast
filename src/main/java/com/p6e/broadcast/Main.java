package com.p6e.broadcast;


import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;
import com.p6e.broadcast.channel.P6eChannelCallback;
import com.p6e.broadcast.channel.huomao.P6eHuoMaoChannel;
import com.p6e.broadcast.channel.huomao.P6eHuoMaoChannelMessage;
import com.p6e.broadcast.common.P6eLoggerCommon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.List;


public class Main {



    public static void main(String[] args) throws InterruptedException {
//        P6eDouYuChannel p = P6eDouYuChannel.create("32892", messages -> {
//            for (P6eDouYuChannelMessage message : messages) {
//                System.out.println(message.__text__());
//                for (Object key : message.data().keySet()) {
//                    Object o = message.data().get(key);
//                    System.out.println(key + "   " + o);
//                }
//            }
//        });
//        Thread.sleep(20000);
//        p.close();


//        P6eBliBliChannel.create("7734200", messages -> {
//            for (P6eBliBliChannelMessage message : messages) {
//                System.out.println(message.data());
//            }
//        });


        P6eLoggerCommon.init();

        P6eHuoMaoChannel.create("http://www.huomao.com/880277", new P6eChannelCallback.HuoMao() {
            @Override
            public void execute(List<P6eHuoMaoChannelMessage> messages) {
                for (P6eHuoMaoChannelMessage message : messages) {
                    System.out.println("[ 1 ] ==> " + message.data());
                }
            }
        });

        Thread.sleep(10000);

        P6eHuoMaoChannel.destroy();

//        P6eHuoMaoChannel.create(13344, new P6eChannelCallback.HuoMao() {
//            @Override
//            public void execute(List<P6eHuoMaoChannelMessage> messages) {
//                for (P6eHuoMaoChannelMessage message : messages) {
//                    System.out.println("[ 2 ] ==> " + message.data());
//                }
//            }
//        });
    }

}
