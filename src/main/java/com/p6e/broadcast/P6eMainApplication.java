package com.p6e.broadcast;

import com.p6e.broadcast.channel.P6eChannelAbstract;
import com.p6e.broadcast.channel.blibli.P6eBliBliChannel;
import com.p6e.broadcast.channel.blibli.P6eBliBliChannelMessage;
import com.p6e.broadcast.channel.douyu.P6eDouYuChannel;
import com.p6e.broadcast.channel.douyu.P6eDouYuChannelMessage;
import com.p6e.broadcast.channel.huomao.P6eHuoMaoChannel;
import com.p6e.broadcast.channel.huomao.P6eHuoMaoChannelMessage;
import com.p6e.broadcast.channel.lognzhu.P6eLongZhuChannel;
import com.p6e.broadcast.channel.lognzhu.P6eLongZhuChannelMessage;
import com.p6e.broadcast.common.P6eLoggerCommon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class P6eMainApplication {

    /** 日志 */
    private static final Logger logger = LoggerFactory.getLogger(P6eMainApplication.class);

    /**
     * 直播平台4.虎牙直播
     *
     * 直播平台5.花椒直播
     *
     * 直播平台6.企鹅电竞
     *
     * 战旗
     *
     * YY
     *
     * 花椒
     *
     * 触手tv
     * // 轮询机制
     * // https://chat.chushou.tv/chat/get.htm?callback=jQuery11020368393905666204_1588376287431&style=2&roomId=93857592&breakpoint=2185_1588376438612_0_0&_=1588376287531
     *
     * 其他的都是废物了
     */
    public static void main(String[] args) {

        /* 日志的初始化 */
        P6eLoggerCommon.init();

        P6eLongZhuChannel p6eLongZhuChannel = P6eLongZhuChannel.create("http://star.longzhu.com/sk2", messages -> {
            for (P6eLongZhuChannelMessage message : messages) {
                logger.info("[ Long Zhu ] ==> " + message.toString());
            }
        });

        if (1 == 1) return;

        /* 斗鱼连接弹幕房间 */
        P6eDouYuChannel p6eDouYuChannel = P6eDouYuChannel.create("288016", messages -> {
            for (P6eDouYuChannelMessage message : messages) {
                logger.info("[ DOU YU ] ==> " + message.data().toString());
            }
        });

        /* BliBli 连接弹幕房间 */
        P6eBliBliChannel p6eBliBliChannel = P6eBliBliChannel.create("7734200", messages -> {
            for (P6eBliBliChannelMessage message : messages) {
                logger.info("[ BLI BLI ] ==> " + message.data().toString());
            }
        });

        /* 火猫连接弹幕房间 */
        P6eHuoMaoChannel p6eHuoMaoChannel = P6eHuoMaoChannel.create("http://www.huomao.com/138569", messages -> {
            for (P6eHuoMaoChannelMessage message : messages) {
                logger.info("[ HUO MAO ] ==> " + message.data().toString());
            }
        });

        /* 龙珠连接弹幕房间 */
//        P6eLongZhuChannel p6eLongZhuChannel = P6eLongZhuChannel.create("http://star.longzhu.com/sk2", messages -> {
//            for (P6eLongZhuChannelMessage message : messages) {
//                logger.info("[ Long Zhu ] ==> " + message.toString());
//            }
//        });

        /* 关闭的方法 */
        // p6eDouYuChannel.close();
        // p6eBliBliChannel.close();
        // p6eHuoMaoChannel.close();

        /* 摧毁的方法 */
        // P6eChannelAbstract.destroy();
    }

}
