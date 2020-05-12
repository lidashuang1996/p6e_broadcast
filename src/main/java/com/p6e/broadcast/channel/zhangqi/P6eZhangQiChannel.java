package com.p6e.broadcast.channel.zhangqi;


import com.p6e.broadcast.channel.P6eChannelAbstract;
import com.p6e.broadcast.channel.P6eChannelCallback;
import com.p6e.broadcast.channel.P6eChannelTimeCallback;
import com.p6e.broadcast.common.P6eToolCommon;
import com.p6e.netty.websocket.client.P6eWebSocketClient;
import com.p6e.netty.websocket.client.actuator.P6eActuatorDefaultAsync;
import com.p6e.netty.websocket.client.config.P6eConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * ZhangQi 直播 https://www.zhanqi.tv/
 * ZhangQi 直播房间消息连接器
 * @version 1.0
 */
public class P6eZhangQiChannel extends P6eChannelAbstract {

    public static void main(String[] args) {
        new P6eZhangQiChannel("https://www.zhanqi.tv/152646472", messages -> {

        });
    }

    /** 日志对象注入 */
    private final static Logger logger = LoggerFactory.getLogger(P6eZhangQiChannel.class);

    /** ZhangQi 房间 ID */
    private String rid;

    /** Web Socket 会话客户端 ID */
    private String clientId;

    /** ZhangQi 回调函数 */
    private P6eChannelCallback.ZhangQi callback;

    /** ZhangQi 配置信息 */
    private P6eZhangQiChannelInventory inventory;

    /**
     * 创建 P6eZhangQiChannel 实例对象
     * @param urlPath 房间的 rid
     * @param callback 接收消息后的回调函数
     * @return P6eZhangQiChannel 实例化的对象
     */
    public static P6eZhangQiChannel create(String urlPath, P6eChannelCallback.ZhangQi callback) {
        if (callback == null) throw new RuntimeException("P6eChannelCallback.ZhangQi null");
        return new P6eZhangQiChannel(urlPath, callback);
    }

    /**
     * 构造方法
     * @param callback 接收消息后的回调函数
     */
    private P6eZhangQiChannel(String urlPath, P6eChannelCallback.ZhangQi callback) {
        this.callback = callback;
        this.inventory = new P6eZhangQiChannelInventory(urlPath);
        this.rid = this.inventory.getRid();
        this.connect();
    }

    /**
     * 连接战旗房间
     */
    protected void connect() {
        // 修改状态为连接中
        this.setStatus(CONNECT_STATUS);
        Map<String, Object> top = new Hashtable<>();
        top.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.132 Safari/537.36");
        // Web Socket 连接房间信息系统
        System.out.println(inventory.getWebSocketUrl());
        clientApplication.connect(
                new P6eConfig(inventory.getWebSocketUrl(), top, new P6eActuatorDefaultAsync() {

                    /** 时间回调触发器的配置信息 */
                    private P6eChannelTimeCallback.Config config;

                    @Override
                    public void onOpen(P6eWebSocketClient webSocket) {
                        clientId = webSocket.getId();

                        logger.debug("ZhangQi onOpenAsync [ CLIENT: " + webSocket.getId() + ", RID: " + rid + " ]");

                        // 发送登录的消息
//                        webSocket.sendBinaryMessage(messageEncoder(inventory.getSvrisokreq(), inventory.getSvrisokreqBytes()));
//                        webSocket.sendBinaryMessage(messageEncoder(inventory.getLoginreq(), inventory.getLoginreqBytes()));

                        // 创建定时器
                        config = new P6eChannelTimeCallback.Config(90, true, true, config ->
                                webSocket.sendBinaryMessage(messageEncoder(inventory.getPant(), inventory.getPantBytes())));

                        // 启动定时器
//                        P6eChannelTimeCallback.addConfig(config);
                    }

                    @Override
                    public void onClose(P6eWebSocketClient webSocket) {
                        logger.debug("ZhangQi onCloseAsync [ CLIENT: " + webSocket.getId() + ", RID: " + rid + " ]");
                        if (this.config != null) P6eChannelTimeCallback.removeConfig(this.config);
                        if (!isClose()) {
                            logger.error("ZhangQi onCloseAsync [ CLIENT: " + webSocket.getId() + ", RID: " + rid + " ]" +
                                    " ==> retry " + incrementRetry() + ", retryCount" + getRetryCount());
                            reconnect(); // 重新连接
                        }
                    }

                    @Override
                    public void onError(P6eWebSocketClient webSocket, Throwable throwable) {
                        logger.error("ZhangQi onErrorAsync [ CLIENT: "
                                + webSocket.getId() + ", RID: " + rid + " ] ==> " + throwable.getMessage());
                    }

                    @Override
                    public void onMessageText(P6eWebSocketClient webSocket, String message) {
                        logger.debug("ZhangQi onMessageTextAsync [ CLIENT: "
                                + webSocket.getId() + ", RID: " + rid + " ] ==> " + message);
                    }

                    @Override
                    public void onMessageBinary(P6eWebSocketClient webSocket, byte[] message) {
                        System.out.println(new String(message));
//                        List<Source> sources = messageDecoder(message);
//                        List<P6eBliBliChannelMessage> messages = new ArrayList<>();
//                        for (Source source : sources) {
//                            // 重置错误的连接次数
//                            if (source.type == inventory.getLoginResultType()) resetReconnect();
//                            messages.add(P6eBliBliChannelMessage.build(source.bytes, source.type, source.content));
//                        }
//                        if (callback != null) callback.execute(messages);
                    }

                    @Override
                    public void onMessagePong(P6eWebSocketClient webSocket, byte[] message) {
                        logger.debug("ZhangQi onMessagePongAsync [ CLIENT: "
                                + webSocket.getId() + ", RID: " + rid + " ] ==> " + new String(message));
                    }

                    @Override
                    public void onMessagePing(P6eWebSocketClient webSocket, byte[] message) {
                        logger.debug("ZhangQi onMessagePingAsync [ CLIENT: "
                                + webSocket.getId() + ", RID: " + rid + " ] ==> " + new String(message));
                    }

                    @Override
                    public void onMessageContinuation(P6eWebSocketClient webSocket, byte[] message) {
                        logger.debug("ZhangQi onMessageContinuationAsync [ CLIENT: "
                                + webSocket.getId() + ", RID: " + rid + " ] ==> " + new String(message));
                    }
                }));
    }

    /**
     * 关闭 BliBli 的房间信息服务器
     */
    @Override
    public void close() {
        if (clientApplication != null) {
            this.removeCache();
            this.setStatus(CLOSE_STATUS);
            clientApplication.close(clientId);
        } else throw new RuntimeException("BliBli client application null");
    }

    @Override
    protected void dump() {
        if (clientApplication != null) {
            this.setStatus(CLOSE_STATUS);
            clientApplication.close(clientId);
        }
    }

    /**
     * ZhangQi 发送消息
     * ZhangQi 消息 = 消息头部
     *              (
     *                  消息内容 [2]
     *                  消息头模式 [ 小端模式(4) 恒为 0 ]
     *                  消息头内容长度 [ 小端模式(4) ]
     *                  消息内容 [2]
     *              )
     *         + 消息内容
     * @param message 发送的消息内容
     * @param bytes 发送消息的类型
     */
    private byte[] messageEncoder(String message, byte[] bytes) {
        int len = message.length();
        byte[] result = new byte[12 + len];
        P6eToolCommon.arrayJoinByte(
                result,
                new byte[] { bytes[0], bytes[1] },
                P6eToolCommon.intToBytesLittle(0),
                P6eToolCommon.intToBytesLittle(len),
                message.getBytes(StandardCharsets.UTF_8),
                new byte[] { bytes[2], bytes[3] }
        );
        return result;
    }

    /**
     * ZhangQi 接收消息
     * ZhangQi 消息 = 消息头部
     *              (
     *                  消息内容 [2]
     *                  消息头模式 [ 小端模式(4) 恒为 0 ]
     *                  消息头内容长度 [ 小端模式(4) ]
     *                  消息内容 [2]
     *              )
     *         + 消息内容
     * @param data 接收消息的类型
     */
    private List<Source> messageDecoder(byte[] data) {
        int index = 0;
        List<Source> sources = new ArrayList<>();
        while (true) {
            if (data.length - index >= 16) {

                // 内容1 部分
                byte[] len1Bytes = new byte[]{data[index++], data[index++], data[index++], data[index++]};
                int len1 = P6eToolCommon.bytesToIntBig(len1Bytes);

                // 内容2 部分
                byte[] len2Bytes = new byte[]{data[index++], data[index++]};
                int len2 = len2Bytes[0] + len2Bytes[1];

                // 内容3 部分
                byte[] agreementBytes = new byte[]{data[index++], data[index++]};
                int agreement = agreementBytes[0] + agreementBytes[1];

                // 内容4 部分
                byte[] typeBytes = new byte[]{data[index++], data[index++], data[index++], data[index++]};
                int type = P6eToolCommon.bytesToIntBig(typeBytes);

                // 内容5 部分
                byte[] spareBytes = new byte[]{data[index++], data[index++], data[index++], data[index++]};
                int spare = P6eToolCommon.bytesToIntBig(spareBytes);

                // 验证是否符合接收消息的格式
                if (len1 > 16 && len2 == 16 && spare == 0) {
                    byte[] contentBytes = new byte[len1];
                    P6eToolCommon.arrayJoinByte(contentBytes, len1Bytes, len2Bytes, agreementBytes, typeBytes, spareBytes);
                    byte[] messageByte = new byte[len1 - 16];
                    for (int _index = index; index < _index + len1 - 16; index++) {
                        messageByte[index - _index] = data[index];
                    }
                    P6eToolCommon.arrayJoinByte(contentBytes, messageByte);
                    if (agreement == 2) {
                        // 发送协议为 2 的类型的数据的时候，可能是多条和并推送过来的
                        // 需要拆开为一条条的数据
                        int z = 0;
                        byte[] zBytes = P6eToolCommon.decompressZlib(messageByte);
                        while (zBytes.length > z) {
                            int zLen = P6eToolCommon.bytesToIntBig(new byte[] { zBytes[z], zBytes[z + 1], zBytes[z + 2], zBytes[z + 3] });
                            byte[] itemBytes = new byte[zLen];
                            for (int i = z; z < i + zLen; z++) itemBytes[z - i] = zBytes[z];
                            sources.add(new Source(itemBytes, type, new String(itemBytes, StandardCharsets.UTF_8).substring(16)));
                        }
                    } else sources.add(new Source(contentBytes, type, new String(messageByte, StandardCharsets.UTF_8)));
                }
            } else break;
        }
        return sources;
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
