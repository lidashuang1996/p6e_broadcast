package com.p6e.broadcast.channel.douyu;

import com.p6e.broadcast.channel.P6eChannelAbstract;
import com.p6e.broadcast.channel.P6eChannelCallback;
import com.p6e.broadcast.channel.P6eChannelTimeCallback;
import com.p6e.broadcast.common.P6eToolCommon;
import com.p6e.netty.websocket.client.P6eWebSocketClient;
import com.p6e.netty.websocket.client.actuator.P6eActuatorDefault;
import com.p6e.netty.websocket.client.config.P6eConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * 斗鱼直播 http://www.huomao.com/
 * 斗鱼直播房间消息连接器
 * @version 1.0
 */
public class P6eDouYuChannel extends P6eChannelAbstract {

    /** 日志对象注入 */
    private final static Logger logger = LoggerFactory.getLogger(P6eDouYuChannel.class);

    /** 斗鱼房间 ID */
    private String rid;

    /** Web Socket 会话客户端 ID */
    private String clientId;

    /** 斗鱼回调函数 */
    private P6eChannelCallback.DouYu callback;

    /** 斗鱼配置信息 */
    private P6eDouYuChannelInventory inventory;

    /**
     * 创建 P6eDouYuChannel 实例对象
     * @param rid 房间 ID
     * @param callback 接收消息后的回调函数
     * @return P6eDouYuChannel 实例化的对象
     */
    public static P6eDouYuChannel create(String rid, P6eChannelCallback.DouYu callback) {
        if (callback == null) throw new RuntimeException("P6eChannelCallback.DouYu null");
        return new P6eDouYuChannel(rid, callback);
    }

    /**
     * 构造方法
     * @param rid 房间的 ID
     * @param callback 接收消息后的回调函数
     */
    private P6eDouYuChannel(String rid, P6eChannelCallback.DouYu callback) {
        this.rid = rid;
        this.callback = callback;
        this.inventory = new P6eDouYuChannelInventory(rid);
        this.connect();
    }

    /**
     * 连接斗鱼房间
     */
    protected void connect() {
        // 修改状态为连接中
        this.setStatus(CONNECT_STATUS);
        P6eConfig config = new P6eConfig(inventory.getWebSocketUrl(), new P6eActuatorDefault() {

            /** 时间回调触发器的配置信息 */
            private P6eChannelTimeCallback.Config config;

            @Override
            public void onOpen(P6eWebSocketClient webSocket) {
                logger.debug("DouYu onOpenAsync [ CLIENT: " + webSocket.getId() + ", RID: " + rid + " ]");
                // 1. 发送登录信息
                webSocket.sendBinaryMessage(messageEncoder(inventory.getLoginInfo()));
                // 2. 发送加入组信息
                webSocket.sendBinaryMessage(messageEncoder(inventory.getGroupInfo()));
                // 3. 发送接受全部弹幕礼物和数据
                webSocket.sendBinaryMessage(messageEncoder(inventory.getAllGiftInfo()));
                // 4. 发送心跳消息
                this.config = new P6eChannelTimeCallback.Config(45, true, true,
                        config -> webSocket.sendBinaryMessage(messageEncoder(inventory.getPant())));
                P6eChannelTimeCallback.addConfig(this.config);
            }

            @Override
            public void onClose(P6eWebSocketClient webSocket) {
                clientId = webSocket.getId();
                logger.debug("DouYu onCloseAsync [ CLIENT: " + webSocket.getId() + ", RID: " + rid + " ]");
                if (this.config != null) P6eChannelTimeCallback.removeConfig(this.config);
                if (!isClose()) {
                    logger.error("DouYu onCloseAsync [ CLIENT: " + webSocket.getId() + ", RID: " + rid + " ]" +
                            " ==> retry " + incrementRetry() + ", retryCount" + getRetryCount());
                    reconnect(); // 重新连接
                }
            }

            @Override
            public void onError(P6eWebSocketClient webSocket, Throwable throwable) {
                logger.error("DouYu onErrorAsync [ CLIENT: "
                        + webSocket.getId() + ", RID: " + rid + " ] ==> " + throwable.getMessage());
            }

            @Override
            public void onMessageText(P6eWebSocketClient webSocket, String message) {
                logger.debug("DouYu onMessageTextAsync [ CLIENT: "
                        + webSocket.getId() + ", RID: " + rid + " ] ==> " + message);
            }

            @Override
            public void onMessageBinary(P6eWebSocketClient webSocket, byte[] message) {
                List<Source> sources = messageDecoder(message);
                List<P6eDouYuChannelMessage> messages = new ArrayList<>();
                for (Source source : sources) {
                    // 包含 type@=loginres 标识连接成功到弹幕服务器
                    if (source.content.contains("type@=loginres")) resetReconnect();
                    messages.add(P6eDouYuChannelMessage.build(source.bytes, source.content));
                }
                callback.execute(messages);
            }

            @Override
            public void onMessagePong(P6eWebSocketClient webSocket, byte[] message) {
                logger.debug("DouYu onMessagePongAsync [ CLIENT: "
                        + webSocket.getId() + ", RID: " + rid + " ] ==> " + new String(message));
            }

            @Override
            public void onMessagePing(P6eWebSocketClient webSocket, byte[] message) {
                logger.debug("DouYu onMessagePingAsync [ CLIENT: "
                        + webSocket.getId() + ", RID: " + rid + " ] ==> " + new String(message));
            }

            @Override
            public void onMessageContinuation(P6eWebSocketClient webSocket, byte[] message) {
                logger.debug("DouYu onMessageContinuationAsync [ CLIENT: "
                        + webSocket.getId() + ", RID: " + rid + " ] ==> " + new String(message));
            }
        });
//        config.setNettyLoggerBool(true);
//        config.setNettyLogLevel(P6eConfig.LogLevel.DEBUG);
        clientApplication.connect(config);
    }

    /**
     * 关闭斗鱼的房间信息服务器
     */
    @Override
    public void close() {
        if (clientApplication != null) {
            this.removeCache();
            this.setStatus(CLOSE_STATUS);
            clientApplication.close(clientId);
        } else throw new RuntimeException("DouYu client application null");
    }

    @Override
    protected void dump() {
        if (clientApplication != null) {
            this.setStatus(CLOSE_STATUS);
            clientApplication.close(clientId);
        }
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
     * @param message  发送的消息内容
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
     */
    private List<Source> messageDecoder(byte[] data) {
        int index = 0;
        List<Source> result = new ArrayList<>();
        while (true) {
            if (data.length - index >= 4) {
                // 消息总长度
                byte[] lenBytes1 = new byte[] { data[index++], data[index++], data[index++], data[index++] };
                int len1 = P6eToolCommon.bytesToIntLittle(lenBytes1);
                if (len1 > 8 && data.length >= (index + len1)) {

                    // 消息总长度
                    byte[] lenBytes2 = new byte[] { data[index++], data[index++], data[index++], data[index++] };
                    int len2 = P6eToolCommon.bytesToIntLittle(lenBytes2);

                    // 消息类型
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
        /** 构造方法注入数据 */
        private byte[] bytes;
        /** 构造方法注入数据 */
        private String content;

        /** 构造方法注入数据 */
        Source(byte[] bytes, String content) {
            this.bytes = bytes;
            this.content = content;
        }
    }
}
