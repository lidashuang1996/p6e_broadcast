# p6e_broadcast


## p6e_broadcast 介绍

> p6e_broadcast 是用来获取国内各大直播平台房间信息（发送的弹幕数据，赠送的礼物数据，等）的 JAVA 应用。目前支持的平台有斗鱼直播（ https://www.douyu.com/ ），火猫直播（ http://www.huomao.com/ ）, BliBli 直播（ https://live.bilibili.com/ ）

## p6e_broadcast 使用

### 连接弹幕房间

``` java
/* 日志的初始化 */
P6eLoggerCommon.init();

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
```



### 关闭的方法

``` java
/* 关闭的方法 */
p6eDouYuChannel.close();
p6eBliBliChannel.close();
p6eHuoMaoChannel.close();
```



### 摧毁的方法

```
P6eChannelAbstract.destroy();
```



## p6e_broadcast 环境

+ com.google.code.gson
+ org.slf4j
+ ch.qos.logback
+ com.p6e.netty
+ junit





