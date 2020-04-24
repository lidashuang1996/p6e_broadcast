package com.p6e.broadcast.channel.huomao;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 火猫直播 http://www.huomao.com/
 *
 * 获取火猫直播房间信息的测试类
 * 1. 连接火猫直播房间  房间 URL 方式
 * 2. 连接火猫直播房间 房间 ID 方式
 * 3. 连接火猫直播房间 房间 URL 方式 ==> 房间 URL 错误
 * 4. 连接火猫直播房间 房间 URL 方式 ==> 房间 URL 不存在错误
 * 5. 连接火猫直播房间 房间 ID 方式 ==> 房间 ID 不存在
 * @version 1.0
 */
public class HuoMaoTest {

    /** 日志组件的导入 */
    private Logger logger = LoggerFactory.getLogger(HuoMaoTest.class);

    /**
     * 连接火猫房间信息服务器
     * 采用火猫房间的 URL 创建对象，连接信息服务器
     * @throws InterruptedException 阻塞主线程异常
     */
    @Test
    public void create1() throws InterruptedException {
        P6eHuoMaoChannel.create("http://www.huomao.com/880277", messages -> {
            for (P6eHuoMaoChannelMessage message : messages) {
                logger.info("[ type ] => " + message.___type__() + " [ byte ] => " + message.__byte__().length
                        + " [ text ] => " + message.__text__() + " [ data ] => " + message.data());
            }
        });

        Thread.sleep(Integer.MAX_VALUE);
    }

    /**
     * 连接火猫房间信息服务器
     * 采用火猫房间的 ID 创建对象，连接信息服务器
     * @throws InterruptedException 阻塞主线程异常
     */
    @Test
    public void create2() throws InterruptedException {
        P6eHuoMaoChannel.create(13344, messages -> {
            for (P6eHuoMaoChannelMessage message : messages) {
                logger.info("[ type ] => " + message.___type__() + " [ byte ] => " + message.__byte__().length
                        + " [ text ] => " + message.__text__() + " [ data ] => " + message.data());
            }
        });

        Thread.sleep(Integer.MAX_VALUE);
    }

    /**
     * 异常 => 连接不存的错误的 URL
     * 这个 URL 不符合 https://www.huomao.com/ 直接抛出异常
     */
    @Test
    public void errorCreate1() {
        P6eHuoMaoChannel.create("http://baidu.com", messages -> {
            for (P6eHuoMaoChannelMessage message : messages) {
                logger.info("[ type ] => " + message.___type__() + " [ byte ] => " + message.__byte__().length
                        + " [ text ] => " + message.__text__() + " [ data ] => " + message.data());
            }
        });
    }

    /**
     * 异常 => 连接不存的房间页面
     * 不存在这个火猫直播的房间号会到 404 页面，也就取不到当前的房间 ID 号
     */
    @Test
    public void errorCreate2() {
        P6eHuoMaoChannel.create("https://www.huomao.com/8787878", messages -> {
            for (P6eHuoMaoChannelMessage message : messages) {
                logger.info("[ type ] => " + message.___type__() + " [ byte ] => " + message.__byte__().length
                        + " [ text ] => " + message.__text__() + " [ data ] => " + message.data());
            }
        });
    }

    /**
     * 异常 => 连接不存的房间
     * 表明连接成功，因为不存在这样的房间也就收不到这个房间的消息
     * @throws InterruptedException 阻塞主线程异常
     */
    @Test
    public void errorCreate3() throws InterruptedException {
        P6eHuoMaoChannel.create(33333333, messages -> {
            for (P6eHuoMaoChannelMessage message : messages) {
                logger.info("[ type ] => " + message.___type__() + " [ byte ] => " + message.__byte__().length
                        + " [ text ] => " + message.__text__() + " [ data ] => " + message.data());
            }
        });
        Thread.sleep(Integer.MAX_VALUE);
    }

}
