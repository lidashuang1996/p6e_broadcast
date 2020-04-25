package com.p6e.broadcast;

import com.p6e.broadcast.channel.blibli.P6eBliBliChannel;
import com.p6e.broadcast.channel.blibli.P6eBliBliChannelMessage;
import com.p6e.broadcast.channel.douyu.P6eDouYuChannel;
import com.p6e.broadcast.channel.douyu.P6eDouYuChannelMessage;
import com.p6e.broadcast.channel.huomao.P6eHuoMaoChannel;
import com.p6e.broadcast.channel.huomao.P6eHuoMaoChannelMessage;
import com.p6e.broadcast.common.P6eLoggerCommon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class P6eMainApplication {

    /** 日志 */
    private static final Logger logger = LoggerFactory.getLogger(P6eMainApplication.class);

    public static void main(String[] args) {

        /* 日志的初始化 */
        P6eLoggerCommon.init();

        /* 斗鱼连接弹幕房间 */
        P6eDouYuChannel.create("288016", messages -> {
            for (P6eDouYuChannelMessage message : messages) {
                logger.info("[ DOU YU ] ==> " + message.data().toString());
            }
        });

        /* BliBli 连接弹幕房间 */
        P6eBliBliChannel.create("7734200", messages -> {
            for (P6eBliBliChannelMessage message : messages) {
                logger.info("[ BLI BLI ] ==> " + message.data().toString());
            }
        });

        /* 火猫连接弹幕房间 */
        P6eHuoMaoChannel.create("http://www.huomao.com/138569", messages -> {
            for (P6eHuoMaoChannelMessage message : messages) {
                logger.info("[ HUO MAO ] ==> " + message.data().toString());
            }
        });

    }

}
