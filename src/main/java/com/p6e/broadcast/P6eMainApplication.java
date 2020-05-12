package com.p6e.broadcast;

import com.p6e.broadcast.channel.blibli.P6eBliBliChannel;
import com.p6e.broadcast.channel.blibli.P6eBliBliChannelMessage;
import com.p6e.broadcast.channel.douyu.P6eDouYuChannel;
import com.p6e.broadcast.channel.douyu.P6eDouYuChannelMessage;
import com.p6e.broadcast.common.P6eLoggerCommon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 1. blibli    ok!
 * 2. douyu     ok!
 * 3. huomao    ok!
 * 4. kuaishou  ok!
 * 5. huya
 * 6. huajiao
 * 7. qier
 * 8. zhanqi
 * 9. YY
 * 10. chushou
 */
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
     *
     *
     *
     *
     * {data: {webSocketInfo: {,…}}}
     * data: {webSocketInfo: {,…}}
     * webSocketInfo: {,…}
     * token: "UN0PyWJe4JD7RjxZWy+NcAy09NsInj/69z2Y4mq7as8yX22l2UuGADc91hGN5ZLGcQqPggjLB5U7fxLaaW7Mmv1J+mW5Da/6VU1Vup/EU6ni/Cy94hxjOLM0QytYOGWubC+nN1q3Y5PfnjgcQ5AI2g=="
     * webSocketUrls: ["wss://live-ws-pg-group6.kuaishou.com/websocket"]
     * 0: "wss://live-ws-pg-group6.kuaishou.com/websocket"
     * __typename: "WebSocketInfoResult"
     *
     *
     *
     *
     * 00000000: 08c8 011a c801 0a98 0155 4e30 5079 574a  .........UN0PyWJ
     * 00000001: 6534 4a44 3752 6a78 5a57 792b 4e63 4179  e4JD7RjxZWy+NcAy
     * 00000002: 3039 4e73 496e 6a2f 3639 7a32 5934 6d71  09NsInj/69z2Y4mq
     * 00000003: 3761 7338 7958 3232 6c32 5575 4741 4463  7as8yX22l2UuGADc
     * 00000004: 3931 6847 4e35 5a4c 4763 5171 5067 676a  91hGN5ZLGcQqPggj
     * 00000005: 4c42 3555 3766 784c 6161 5737 4d6d 7631  LB5U7fxLaaW7Mmv1
     * 00000006: 4a2b 6d57 3544 612f 3656 5531 5675 702f  J+mW5Da/6VU1Vup/
     * 00000007: 4555 366e 692f 4379 3934 6878 6a4f 4c4d  EU6ni/Cy94hxjOLM
     * 00000008: 3051 7974 594f 4757 7562 432b 6e4e 3171  0QytYOGWubC+nN1q
     * 00000009: 3359 3550 666e 6a67 6351 3541 4932 673d  3Y5PfnjgcQ5AI2g=
     * 0000000a: 3d12 0b4e 4573 314f 6761 666b 5a63 3a1e  =..NEs1OgafkZc:.
     * 0000000b: 426f 4f41 7958 6630 6e43 7161 4a79 7665  BoOAyXf0nCqaJyve
     * 0000000c: 5f31 3538 3833 3737 3833 3036 3731       _1588377830671
     *
     *
     * {"data":{"webLiveDetail":
     * {"liveStream":{
     * "playUrls":[],
     * "gameInfo":{"type":null}},
     * "feedInfo":{"pullCycleMillis":null,"__typename":"FeedInfo"},
     * "watchingInfo":{"likeCount":null,"watchingCount":"","__typename":"WatchingInfo"},
     * "noticeList":[],"fastComments":[],
     * "commentColors":[],"
     * moreRecommendList":[
     * {"user":{"id":"Yi6666666","avatar":"https://tx2.a.yximgs.com/uhead/AB/2019/03/12/17/BMjAxOTAzMTIxNzMyMDZfOTMyMjg2NTNfM19oZDI5Ml8w.jpg",
     * "name":"槿夕⭐ 和平精英","__typename":"User"},"watchingCount":"2.7w","poster":"https://live1.static.yximgs.com/live/game/screenshot/QaUxYsNBQQQ~1588381993472~1","coverUrl":"https://js2.a.yximgs.com/uhead/AB/2020/05/02/08/BMjAyMDA1MDIwODE3MzNfOTMyMjg2NTNfNjQwMTQ1MDUwNF9sdg==.jpg","caption":"淘汰2020 没成功不下播","id":"QaUxYsNBQQQ","playUrls":[{"quality":"blueRay","url":"https://ali-adaptive.pull.yximgs.com/gifshow/QaUxYsNBQQQ.flv?auth_key=1588468463-0-0-70a6521abbc3230ed68f9407ced565ca&highTraffic=1&oidc=alihb","__typename":"FlvPlayUrl"},{"quality":"super","url":"https://ali-adaptive.pull.yximgs.com/gifshow/QaUxYsNBQQQ_hd4000tp.flv?auth_key=1588468463-0-0-de1bfcdf7d52d80e98a3ae6de80982d9&tsc=origin&highTraffic=1&oidc=alihb","__typename":"FlvPlayUrl"},{"quality":"high","url":"https://ali-adaptive.pull.yximgs.com/gifshow/QaUxYsNBQQQ_hd2000tp.flv?auth_key=1588468463-0-0-726d3e7328867c7a73ce599095ff7849&tsc=origin&highTraffic=1&oidc=alihb","__typename":"FlvPlayUrl"},{"quality":"standard","url":"https://ali-adaptive.pull.yximgs.com/gifshow/QaUxYsNBQQQ_sd1000tp.flv?auth_key=1588468463-0-0-78244a5279537a2690e31858f2ffa4f0&tsc=origin&highTraffic=1&oidc=alihb","__typename":"FlvPlayUrl"}],"quality":"blueRay","gameInfo":{"category":"SYXX","name":"和平精英","pubgSurvival":null,"type":22008,"kingHero":null,"__typename":"GameInfo"},"hasRedPack":false,"liveGuess":false,"expTag":"1_u/2000048874980299361_gwr0","__typename":"LiveInfo"},{"user":{"id":"KPL704668133","avatar":"https://tx2.a.yximgs.com/uhead/AB/2020/04/05/09/BMjAyMDA0MDUwOTU0MDJfNzA0NjY4MTMzXzFfaGQ3MzBfODE1_s.jpg","name":"KPL王者荣耀职业联赛","__typename":"User"},"watchingCount":"1638","poster":"https://live4.static.yximgs.com/live/game/screenshot/NEs1OgafkZc
     *
     *https://live.kuaishou.com/m_graphql
     *
     * // 登录
     * 08c8 011a c801 0a98
     * 08ac 0210 011a 0a08
     * 08d4 0210 011a c726
     * 08b6 0210 011a 0e0a
     * 08b6 0210 011a 610a
     * 08b6 0210 011a 8a01
     * 08b6 0210 011a 6d0a
     * 0801 1a07 08da e1cc
     *
     *
     *
     */
    public static void main(String[] args) {

        /* 日志的初始化 */
        P6eLoggerCommon.init();

//        P6eLongZhuChannel p6eLongZhuChannel = P6eLongZhuChannel.create("http://star.longzhu.com/sk2", messages -> {
//            for (P6eLongZhuChannelMessage message : messages) {
//                logger.info("[ Long Zhu ] ==> " + message.toString());
//            }
//        });


        /* 斗鱼连接弹幕房间 */
//        P6eDouYuChannel p6eDouYuChannel1 = P6eDouYuChannel.create("96291", messages -> {
//            for (P6eDouYuChannelMessage message : messages) {
//                logger.info("[ DOU YU 96291 ] ==> " + message.data().toString());
//            }
//        });
//
//        P6eDouYuChannel p6eDouYuChannel2 = P6eDouYuChannel.create("290935", messages -> {
//            for (P6eDouYuChannelMessage message : messages) {
//                logger.info("[ DOU YU 290935 ] ==> " + message.data().toString());
//            }
//        });
//
//        P6eDouYuChannel p6eDouYuChannel3 = P6eDouYuChannel.create("99999", messages -> {
//            for (P6eDouYuChannelMessage message : messages) {
//                logger.info("[ DOU YU 99999 ] ==> " + message.data().toString());
//            }
//        });





        /* BliBli 连接弹幕房间 */
        P6eBliBliChannel p6eBliBliChannel = P6eBliBliChannel.create("10810434", messages -> {
            for (P6eBliBliChannelMessage message : messages) {
                logger.info("[ BLI BLI ] ==> " + message.data().toString());
            }
        });

        /* 火猫连接弹幕房间 */
//        P6eHuoMaoChannel p6eHuoMaoChannel = P6eHuoMaoChannel.create("http://www.huomao.com/138569", messages -> {
//            for (P6eHuoMaoChannelMessage message : messages) {
//                logger.info("[ HUO MAO ] ==> " + message.data().toString());
//            }
//        });

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
