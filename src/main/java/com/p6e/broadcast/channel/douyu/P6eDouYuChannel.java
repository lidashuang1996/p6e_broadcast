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

public class P6eDouYuChannel extends P6eChannelAbstract {

    private final static Logger logger = LoggerFactory.getLogger(P6eDouYuChannel.class);

    private String rid;

    private P6eChannelCallback.DouYu callback;
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
        clientApplication.connect(new P6eProduct(inventory.getWebSocketUrl(), new P6eInstructionsAbstractAsync() {

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
            }

            @Override
            public void onCloseAsync(String id) {
                logger.debug("onCloseAsync [ CLIENT: " + id + ", RID: " + rid + " ]");
                if (this.config != null) P6eChannelTimeCallback.removConfig(this.config);
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

    /**
     * 斗鱼发送消息的加密方式
     * @param message 发送的消息内容
     * @return 发送的消息的 byte 数组
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
     * 斗鱼接收消息的解密方式
     * @param data 接收的消息内容
     * @return List<Source> 接收消息处理后的对象
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


    private static class Source {
        private byte[] bytes;
        private String content;

        public Source(byte[] bytes, String content) {
            this.bytes = bytes;
            this.content = content;
        }
    }
}
