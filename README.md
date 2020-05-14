# P6e Broadcast



## P6e Broadcast 介绍

> p6e_broadcast 是用来获取国内各大直播平台房间信息（发送的弹幕数据，赠送的礼物数据，等）的 JAVA 应用。目前支持的平台有斗鱼直播（ https://www.douyu.com/ ），火猫直播（ http://www.huomao.com/ ）, BliBli 直播（ https://live.bilibili.com/ ）

## P6e Broadcast 使用

### 连接弹幕房间

``` java
/* 日志的初始化 */
P6eLoggerCommon.init();

/* 创建连接房间的时候会创建一个全局的 Web Socket 连接连接器，和一个心跳线程 */
/* 如果需要彻底关闭的，需要执行 destroy 方法*/
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
P6eHuoMaoChannel p6eHuoMaoChannel 
    = P6eHuoMaoChannel.create("http://www.huomao.com/138569", messages -> {
    for (P6eHuoMaoChannelMessage message : messages) {
        logger.info("[ HUO MAO ] ==> " + message.data().toString());
    }
});
```

### 关闭的方法

``` java
/* 关闭是当前连接器的方法 */
p6eDouYuChannel.close();
p6eBliBliChannel.close();
p6eHuoMaoChannel.close();
```

### 摧毁的方法

```java
/* 摧毁的方法 */
/* 关闭当前的所有连接器 */
/* 摧毁创建连接器的对象，以及里面的异步线程池 */
P6eChannelAbstract.destroy();
```

### 自定义创建连接器对象

``` java
/* 需要在创建连接之前调用 */
/* 传入的连接器对象会替换默认的连接器对象 */
P6eChannelAbstract.init(P6eWebsocketClientApplication.run(
    P6eNioModel.class, // 可以自定义的实现，这里是默认的实现
    // 替换异步处理消息的线程池 （用自定义的线程池管理异步回调消息）
    new ThreadPoolExecutor(0, 30, 60L, TimeUnit.SECONDS, new SynchronousQueue<>())
));


// 连接弹幕服的代码
// ...... 
```



## P6e Broadcast 环境

``` xml
<!-- GSON 对象 -->
<dependency>
    <groupId>com.google.code.gson</groupId>
    <artifactId>gson</artifactId>
    <version>2.8.6</version>
</dependency>

<!-- https://mvnrepository.com/artifact/ch.qos.logback/logback-classic -->
<dependency>
    <groupId>ch.qos.logback</groupId>
    <artifactId>logback-classic</artifactId>
    <version>1.2.3</version>
</dependency>

<!-- netty 编写的 Web Socket 客户端 -->
<!-- 这个是我本地的 Mevan 环境下载的依赖包 -->
<!-- https://github.com/lidashuang1996/p6e_netty_websocket_client -->
<!-- 暂时其他用户只能通过 JAR 导入 -->
<dependency>
    <groupId>com.p6e.netty</groupId>
    <artifactId>p6e-netty-websocket-client</artifactId>
    <version>1.0.3</version>
</dependency>

<!-- 测试工具 JAR -->
<dependency>
    <groupId>junit</groupId>
    <artifactId>junit</artifactId>
    <version>4.12</version>
</dependency>
```







