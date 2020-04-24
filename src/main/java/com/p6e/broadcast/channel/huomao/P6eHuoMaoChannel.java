package com.p6e.broadcast.channel.huomao;

import com.p6e.broadcast.channel.P6eChannelAbstract;
import com.p6e.broadcast.channel.P6eChannelCallback;
import com.p6e.broadcast.channel.P6eChannelTimeCallback;
import com.p6e.broadcast.common.P6eToolCommon;
import com.p6e.netty.websocket.client.instructions.P6eInstructionsAbstractAsync;
import com.p6e.netty.websocket.client.product.P6eProduct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * 火猫直播 http://www.huomao.com/
 * 火猫直播房间消息连接器
 * @version 1.0
 */
public class P6eHuoMaoChannel extends P6eChannelAbstract {

    /** 日志对象注入 */
    private final static Logger logger = LoggerFactory.getLogger(P6eHuoMaoChannel.class);

    /** Web Socket 客户端对象 */
    private P6eProduct product;

    /** 火猫房间 ID */
    private String rid;

    /** 火猫回调函数 */
    private P6eChannelCallback.HuoMao callback;

    /** 火猫配置信息 */
    private P6eHuoMaoChannelInventory inventory;

    public static P6eHuoMaoChannel create(String urlPath, P6eChannelCallback.HuoMao callback) {
        if (callback == null) throw new RuntimeException("P6eChannelCallback.HuoMao null");
        return new P6eHuoMaoChannel(urlPath, callback);
    }

    public static P6eHuoMaoChannel create(int rid, P6eChannelCallback.HuoMao callback) {
        if (callback == null) throw new RuntimeException("P6eChannelCallback.HuoMao null");
        return new P6eHuoMaoChannel(rid, callback);
    }

    private P6eHuoMaoChannel(int rid, P6eChannelCallback.HuoMao callback) {
        this.rid = String.valueOf(rid);
        this.callback = callback;
        this.inventory = new P6eHuoMaoChannelInventory(rid);
        this.connect();
    }

    private P6eHuoMaoChannel(String urlPath, P6eChannelCallback.HuoMao callback) {
        this.callback = callback;
        this.inventory = new P6eHuoMaoChannelInventory(urlPath);
        this.rid = this.inventory.getId();
        this.connect();
    }

    /**
     * 连接火猫的房间信息服务器
     */
    protected void connect() {
        // 修改状态为连接中
        this.setStatus(CONNECT_STATUS);
        // Web Socket 连接房间信息系统
        if (clientApplication == null) throw new RuntimeException("client application null");
        else {
            clientApplication.connect(
                    this.product = new P6eProduct(inventory.getWebSocketWssUrl(), new P6eInstructionsAbstractAsync() {

                        /** 时间回调触发器的配置信息 */
                        private P6eChannelTimeCallback.Config config;

                        @Override
                        public void onOpenAsync(String id) {
                            logger.debug("onOpenAsync [ CLIENT: " + id + ", RID: " + rid + " ]");

                            // 发送登录的消息
                            this.sendMessage(BINARY_MESSAGE_TYPE, messageEncoder(inventory.getLoginInfo(), inventory.getLoginType()));

                            // 定时器
                            this.config = new P6eChannelTimeCallback.Config(30, true, true,
                                    config -> this.sendMessage(BINARY_MESSAGE_TYPE, messageEncoder(inventory.getPantInfo(), inventory.getPantType())));
                            P6eChannelTimeCallback.addConfig(this.config);

                        }

                        @Override
                        public void onCloseAsync(String id) {
                            logger.debug("onCloseAsync [ CLIENT: " + id + ", RID: " + rid + " ]");
                            if (this.config != null) P6eChannelTimeCallback.removeConfig(this.config);
                            if (isClose()) {
                                logger.error("onCloseAsync [ CLIENT: " + id + ", RID: " + rid + " ]" +
                                        " ==> retry " + incrementRetry() + ", retryCount" + getRetryCount());
                                reconnect(); // 重新连接
                            }
                        }

                        @Override
                        public void onErrorAsync(String id, Throwable throwable) {
                            logger.error("onErrorAsync [ CLIENT: " + id + ", RID: " + rid + " ] ==> " + throwable.getMessage());
                        }

                        @Override
                        public void onMessageTextAsync(String id, String content) {
                            logger.debug("onMessageTextAsync [ CLIENT: " + id + ", RID: " + rid + " ] ==> " + content);
                        }

                        @Override
                        public void onMessageBinaryAsync(String id, byte[] bytes) {
                            List<Source> sources = messageDecoder(bytes);
                            List<P6eHuoMaoChannelMessage> messages = new ArrayList<>();
                            for (Source source : sources) {
                                if (source.type == inventory.getLoginResultType()) resetReconnect(); // 登录成功，重置错误的次数
                                messages.add(P6eHuoMaoChannelMessage.build(source.bytes, source.type, source.content));
                            }
                            callback.execute(messages);
                        }

                        @Override
                        public void onMessagePongAsync(String id, byte[] bytes) {
                            logger.debug("onMessagePongAsync [ CLIENT: " + id + ", RID: " + rid + " ] ==> " + new String(bytes));
                        }

                        @Override
                        public void onMessagePingAsync(String id, byte[] bytes) {
                            logger.debug("onMessagePingAsync [ CLIENT: " + id + ", RID: " + rid + " ] ==> " + new String(bytes));
                        }

                        @Override
                        public void onMessageContinuationAsync(String id, byte[] bytes) {
                            logger.debug("onMessageContinuationAsync [ CLIENT: " + id + ", RID: " + rid + " ] ==> " + new String(bytes));
                        }
                    }));
        }
    }

    /**
     * 关闭火猫的房间信息服务器
     */
    @Override
    public void close() {
        if (clientApplication != null) {
            this.removeCache();
            this.setStatus(CLOSE_STATUS);
            clientApplication.close(product.getId());
        } else throw new RuntimeException("client application null");
    }

    @Override
    protected void dump() {
        if (clientApplication != null) {
            this.setStatus(CLOSE_STATUS);
            clientApplication.close(product.getId());
        }
    }

    /**
     * 火猫发送消息
     * 火猫消息 = 消息头部
     *              (
     *                  消息总长度 [ 大端模式转换 4]
     *                  消息头部总长度 [ 大端模式转换 2 ]
     *                  消息协议 [ 大端模式转换 2 ] 1 提交数据服务器返回的类型 0 服务器推送的数据
     *                  消息类型 [ 大端模式转换 4 ] {
     *                      7. 登录提交类型
     *                      8. 登录返回类型
     *                      2. 心跳数据提交类型
     *                      3. 心跳服务器返回的类型
     *                      5. 服务端推送的消息类型
     *                  }
     *                  消息XXX [ 小端模式转换 4 ] ( 同消息协议数据 )
     *             )
     *         + 消息内容
     * @param message 消息内容
     * @param type 消息类型
     */
    private byte[] messageEncoder(String message, int type) {
        int len = 16 + message.length();
        byte[] bytes = new byte[len];
        P6eToolCommon.arrayJoinByte(
                bytes,
                P6eToolCommon.intToBytesBig(len),
                new byte[] { 0, 16, 0, 1},
                P6eToolCommon.intToBytesBig(type),
                P6eToolCommon.intToBytesBig(inventory.getSendType()),
                message.getBytes(StandardCharsets.UTF_8)
        );
        return bytes;
    }

    /**
     * 火猫接收消息的处理方法
     * 火猫消息 = 消息头部
     *              (
     *                  消息总长度 [ 大端模式转换 4]
     *                  消息头部总长度 [ 大端模式转换 2 ]
     *                  消息协议 [ 大端模式转换 2 ] 1 提交数据服务器返回的类型 0 服务器推送的数据
     *                  消息类型 [ 大端模式转换 4 ] {
     *                      7. 登录提交类型
     *                      8. 登录返回类型
     *                      2. 心跳数据提交类型
     *                      3. 心跳服务器返回的类型
     *                      5. 服务端推送的消息类型
     *                  }
     *                  消息XXX [ 大端模式转换 4 ] ( 同消息协议数据 )
     *             )
     *         + 消息内容
     * @param data 接收的消息内容
     */
    private List<Source> messageDecoder(byte[] data) {
        int index = 0; // 当前索引
        List<Source> result = new ArrayList<>(); // 消息返回的集合
        while (true) {
            if (data.length - index > 4) {
                // 消息的长度
                byte[] lenBytes = new byte[] { data[index++], data[index++], data[index++], data[index++] };
                int len = P6eToolCommon.bytesToIntBig(lenBytes);
                if (data.length >= (index + len - 4)) {

                    // 内容1 部分
                    byte[] contentBytes1 = new byte[] { data[index++], data[index++] };
                    int content1 = contentBytes1[0] + contentBytes1[1];

                    // 内容2 部分
                    byte[] contentBytes2 = new byte[] { data[index++], data[index++] };
                    int content2 = contentBytes2[0] + contentBytes2[1];

                    // 内容3 部分
                    byte[] contentBytes3 = new byte[] { data[index++], data[index++], data[index++], data[index++] };
                    int content3 = P6eToolCommon.bytesToIntBig(contentBytes3);

                    // 内容4 部分
                    byte[] contentBytes4 = new byte[] { data[index++], data[index++], data[index++], data[index++] };
                    int content4 = P6eToolCommon.bytesToIntBig(contentBytes4);

                    // 全部内容
                    byte[] contentBytes = new byte[len];

                    // 数组内容和并
                    P6eToolCommon.arrayJoinByte(contentBytes, lenBytes, contentBytes1, contentBytes2, contentBytes3, contentBytes4);

                    for (int _index = index; index < _index + len - 16; index++) {
                        contentBytes[index - _index + 16] = data[index];
                    }

                    // 验证数据格式是否正确
                    if (content1 == 16 && content2 == content4) {
                        result.add(new Source(contentBytes, content3, new String(contentBytes, StandardCharsets.UTF_8).substring(16)));
                    }

                }
            } else break;
        }
        return result;
    }


    /**
    * 消息源的模型类，内部使用
    */
    private static class Source {
        /** 构造方法注入数据 */
        private byte[] bytes;
        /** 构造方法注入数据 */
        private int type;
        /** 构造方法注入数据 */
        private String content;

        /** 构造方法注入数据 */
        Source(byte[] bytes, int type, String content) {
            this.bytes = bytes;
            this.type = type;
            this.content = content;
        }
    }

}
