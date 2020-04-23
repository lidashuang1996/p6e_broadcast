package com.p6e.broadcast.channel.douyu;

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
 * 连接斗鱼的房间弹幕服务
 */
public class P6eDouYuChannel extends P6eChannelAbstract {

    // 日志对象
    private final static Logger logger = LoggerFactory.getLogger(P6eDouYuChannel.class);

    private int status = 0;
    private int error = 0;
    private int errorCount = 3;
    private P6eProduct product;

    // 房间 ID
    private String rid;
    // 回调函数
    private P6eChannelCallback.DouYu callback;
    // 斗鱼配置信息
    private P6eDouYuChannelInventory inventory;

    public static P6eDouYuChannel create(String rid, P6eChannelCallback.DouYu callback) {
        if (callback == null) throw new RuntimeException("P6eChannelCallback.DouYu null");
        return new P6eDouYuChannel(rid, callback);
    }

    private P6eDouYuChannel(String rid, P6eChannelCallback.DouYu callback) {
        this.rid = rid;
        this.callback = callback;
        this.inventory = new P6eDouYuChannelInventory(rid);
        this.connect();
    }

    /**
     * 连接斗鱼房间
     */
    private void connect() {
        this.status = 0; // 连接中
        clientApplication.connect(
                this.product = new P6eProduct(inventory.getWebSocketUrl(), new P6eInstructionsAbstractAsync() {

            /**
             * 时间回调触发器的配置信息
             */
            private P6eChannelTimeCallback.Config config;

            @Override
            public void onOpenAsync(String id) {
                logger.debug("onOpenAsync [ CLIENT: " + id + ", RID: " + rid + " ]");
                // 1. 发送登录信息
                this.sendMessage(BINARY_MESSAGE_TYPE, messageEncoder(inventory.getLoginInfo()));
                // 2. 发送加入组信息
                this.sendMessage(BINARY_MESSAGE_TYPE, messageEncoder(inventory.getGroupInfo()));
                // 3. 发送接受全部弹幕礼物和数据
                this.sendMessage(BINARY_MESSAGE_TYPE, messageEncoder(inventory.getAllGiftInfo()));
                // 4. 发送心跳消息
                this.config = new P6eChannelTimeCallback.Config(45, true, true,
                        config -> this.sendMessage(BINARY_MESSAGE_TYPE, messageEncoder(inventory.getPant())));
                P6eChannelTimeCallback.addConfig(this.config);


                error = 0;
                status = 1;
            }

            @Override
            public void onCloseAsync(String id) {
                logger.debug("onCloseAsync [ CLIENT: " + id + ", RID: " + rid + " ]");
                if (this.config != null) P6eChannelTimeCallback.removConfig(this.config);
                if (status != -1) {
                    error ++;
                    logger.error("onCloseAsync [ CLIENT: " + id + ", RID: " + rid + " ]" +
                            " ==> error " + error + ", errorCount" + errorCount);
                    if (error <= errorCount) {
                        logger.info("reconnect [ CLIENT: " + id + ", RID: " + rid + " ]");
                        connect(); // 重新连接
                    }
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
                List<P6eDouYuChannelMessage> messages = new ArrayList<>();
                for (Source source : sources) {
                    messages.add(P6eDouYuChannelMessage.build(source.bytes, source.content));
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

    public void close() {
        this.status = -1; // 关闭
        if (this.product != null) clientApplication.close(this.product.getId());
    }

    /**
     * 斗鱼发送消息
     * 消息类型 689 客户端发送给斗鱼服务器
     * 斗鱼消息 = 消息头部
     *          (
     *              消息总长度 [ 小端模式转换 4 ( 这个不算总长度中 )]
     *              消息总长度 [ 小端模式转换 4 ]
     *              消息类型 [ 小端模式转换 4 ]
     *         )
     *         + 消息内容
     *         + 结束符号 (0)
     */
    private byte[] messageEncoder(String message) {
        int len = 4 + 4 + 1 + message.length();
        byte[] bytes = new byte[4 + len];
        P6eToolCommon.arrayJoinByte(bytes,
                P6eToolCommon.intToBytesLittle(len),
                P6eToolCommon.intToBytesLittle(len),
                P6eToolCommon.intToBytesLittle(inventory.getClientMessageType()),
                message.getBytes(StandardCharsets.UTF_8),
                new byte[] { 0 }
        );
        return bytes;
    }

    /**
     * 斗鱼接收消息
     * 消息类型 690 斗鱼服务器推送给客户端的类型
     * 斗鱼消息 = 消息头部
     *          (
     *              消息总长度 [ 小端模式转换 4 ( 这个不算总长度中 )]
     *              消息总长度 [ 小端模式转换 4 ]
     *              消息类型 [ 小端模式转换 4 ]
     *          )
     *         + 消息内容
     *         + 结束符号 (0)
     */
    private List<Source> messageDecoder(byte[] data) {
        int index = 0;
        List<Source> result = new ArrayList<>();
        while (true) {
            if (data.length - index >= 4) {
                byte[] lenBytes1 = new byte[] { data[index++], data[index++], data[index++], data[index++] };
                int len1 = P6eToolCommon.bytesToIntLittle(lenBytes1);
                if (len1 > 8 && data.length >= (index + len1)) {

                    byte[] lenBytes2 = new byte[] { data[index++], data[index++], data[index++], data[index++] };
                    int len2 = P6eToolCommon.bytesToIntLittle(lenBytes2);

                    byte[] typeBytes = new byte[] { data[index++], data[index++], data[index++], data[index++] };
                    int type = P6eToolCommon.bytesToIntLittle(typeBytes);

                    byte[] contentBytes = new byte[len1 + 4];

                    P6eToolCommon.arrayJoinByte(contentBytes, lenBytes1, lenBytes2, typeBytes);

                    for (int _index = index; index < _index + len1 - 8; index++) {
                        contentBytes[index - _index + 12] = data[index];
                    }

                    // 核对一下数据
                    if (len1 == len2 && type == inventory.getServiceMessageType()) {
                        result.add(new Source(contentBytes, new String(contentBytes).substring(12)));
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
        private byte[] bytes;
        private String content;

        Source(byte[] bytes, String content) {
            this.bytes = bytes;
            this.content = content;
        }
    }
}
