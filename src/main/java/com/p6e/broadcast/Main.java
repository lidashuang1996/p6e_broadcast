package com.p6e.broadcast;


import com.p6e.broadcast.channel.P6eChannelCallback;
import com.p6e.broadcast.channel.blibli.P6eBliBliChannel;
import com.p6e.broadcast.channel.blibli.P6eBliBliChannelMessage;
import com.p6e.broadcast.channel.douyu.P6eDouYuChannel;
import com.p6e.broadcast.channel.douyu.P6eDouYuChannelMessage;
import com.p6e.broadcast.channel.huomao.P6eHuoMaoChannel;
import com.p6e.broadcast.channel.huomao.P6eHuoMaoChannelMessage;
import org.apache.log4j.BasicConfigurator;

import java.util.List;


public class Main {

    public static void main(String[] args) throws InterruptedException {
        // log4j 日志
        BasicConfigurator.configure();
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

        P6eHuoMaoChannel.create("http://www.huomao.com/880277", new P6eChannelCallback.HuoMao() {
            @Override
            public void execute(List<P6eHuoMaoChannelMessage> messages) {
                for (P6eHuoMaoChannelMessage message : messages) {
                    System.out.println("[ 1 ] ==> " + message.data());
                }
            }
        });

        P6eHuoMaoChannel.create(13344, new P6eChannelCallback.HuoMao() {
            @Override
            public void execute(List<P6eHuoMaoChannelMessage> messages) {
                for (P6eHuoMaoChannelMessage message : messages) {
                    System.out.println("[ 2 ] ==> " + message.data());
                }
            }
        });
    }

}
