package com.p6e.broadcast;


import com.p6e.broadcast.channel.douyu.P6eDouYuChannel;
import com.p6e.broadcast.channel.douyu.P6eDouYuChannelMessage;
import org.apache.log4j.BasicConfigurator;


public class Main {

    public static void main(String[] args) throws InterruptedException {
        // log4j 日志
        BasicConfigurator.configure();
        P6eDouYuChannel.create("32892", messages -> {
            for (P6eDouYuChannelMessage message : messages) {
                System.out.println(message.__text__());
                for (Object key : message.data().keySet()) {
                    Object o = message.data().get(key);
                    System.out.println(key + "   " + o);
                }
            }
        });
        Thread.sleep(Integer.MAX_VALUE);
    }

}
