package com.p6e.broadcast.channel.blibli;

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

public class P6eBliBliChannel extends P6eChannelAbstract {
    // 日志对象
    private final static Logger logger = LoggerFactory.getLogger(P6eBliBliChannel.class);

    private int status = 0;
    private int error = 0;
    private int errorCount = 3;
    private P6eProduct product;

    // 房间 ID
    private String rid;
    // 回调函数
    private P6eChannelCallback.BliBli callback;
    // 斗鱼配置信息
    private P6eBliBliChannelInventory inventory;

    public static P6eBliBliChannel create(String rid, P6eChannelCallback.BliBli callback) {
        if (callback == null) throw new RuntimeException("P6eChannelCallback.BliBli null");
        return new P6eBliBliChannel(rid, callback);
    }

    private P6eBliBliChannel(String rid, P6eChannelCallback.BliBli callback) {
        this.rid = rid;
        this.callback = callback;
        this.inventory = new P6eBliBliChannelInventory(rid);
        this.connect();
    }

    /**
     * 连接斗鱼房间
     */
    protected void connect() {
        this.status = 0; // 连接中
        clientApplication.connect(
                this.product = new P6eProduct(inventory.getWebSocketWssUrl(), new P6eInstructionsAbstractAsync() {

                    /**
                     * 时间回调触发器的配置信息
                     */
                    private P6eChannelTimeCallback.Config config;

                    @Override
                    public void onOpenAsync(String id) {
                        logger.debug("onOpenAsync [ CLIENT: " + id + ", RID: " + rid + " ]");

                        this.sendMessage(BINARY_MESSAGE_TYPE, messageEncoder(inventory.getLoginInfo(), inventory.getLoginType()));

                        config = new P6eChannelTimeCallback.Config(30, true, true, config ->
                                this.sendMessage(BINARY_MESSAGE_TYPE, messageEncoder(inventory.getPantInfo(), inventory.getPantType())));

                        P6eChannelTimeCallback.addConfig(config);

                    }

                    @Override
                    public void onCloseAsync(String id) {
                        logger.debug("onCloseAsync [ CLIENT: " + id + ", RID: " + rid + " ]");
                        if (this.config != null) P6eChannelTimeCallback.removeConfig(this.config);
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
                        List<P6eBliBliChannelMessage> messages = new ArrayList<>();
                        for (Source source : sources) {
                            messages.add(P6eBliBliChannelMessage.build(source.bytes, source.type, source.content));
                        }
                        if (callback != null) callback.execute(messages);
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

    @Override
    protected void dump() {

    }


    /**
     * blibli 发送消息
     * blibli 消息 = 消息头部
     *              (
     *                  消息总长度 [ 大端模式转换 (4) ]
     *                  消息头部总长度 [ 数据包头部长度，固定为 16 (2) ]
     *                  数据包协议版本 [
     *                      0	数据包有效负载为未压缩的JSON格式数据
     *                      1	客户端心跳包，或服务器心跳回应（带有人气值）
     *                      2	数据包有效负载为通过zlib压缩后的JSON格式数据
     *                      (2)
     *                  ]
     *                  数据包类型 [
     *                      2	客户端	心跳	不发送心跳包，50-60秒后服务器会强制断开连接
     *                      3	服务器	心跳回应	有效负载为直播间人气值
     *                      5	服务器	通知	有效负载为礼物、弹幕、公告等
     *                      7	客户端	认证（加入房间）	客户端成功建立连接后发送的第一个数据包
     *                      // {"uid":0,"roomid":1938890,"protover":2,"platform":"web","clientver":"1.10.3","type":2,"key":"I1xv9faLSt4UN3_lBqJG_PxZulDTkw1uZHi-wWts0rae3a7SVlyDRm_9g4fFz16BgoOB0Ib-mnXjb32u1nAVmQMOlkbBJvE1XM1C10vGvbePbiwTMQ=="}
     *                      8	服务器	认证成功回应	服务器接受认证包后回应的第一个数据包
     *                      (4)
     *                  ]
     *                  备用字段 [ 固定为 1 (4) ]
     *              )
     *         + 消息内容
     */
    private byte[] messageEncoder(String message, int type) {
        int len = 16 + message.length();
        byte[] bytes = new byte[len];
        P6eToolCommon.arrayJoinByte(
                bytes,
                P6eToolCommon.intToBytesBig(len),
                new byte[] { 0, 16, 0, 1 },
                P6eToolCommon.intToBytesBig(type),
                P6eToolCommon.intToBytesBig(1),
                message.getBytes(StandardCharsets.UTF_8)
        );
        return bytes;
    }

    /**
     * blibli 接收消息
     * blibli 消息 = 消息头部
     *              (
     *                  消息总长度 [ 大端模式转换 (4) ]
     *                  消息头部总长度 [ 数据包头部长度，固定为 16 (2) ]
     *                  数据包协议版本 [
     *                      0	数据包有效负载为未压缩的JSON格式数据
     *                      1	客户端心跳包，或服务器心跳回应（带有人气值）
     *                      2	数据包有效负载为通过zlib压缩后的JSON格式数据
     *                      (2)
     *                  ]
     *                  数据包类型 [
     *                      2	客户端	心跳	不发送心跳包，50-60秒后服务器会强制断开连接
     *                      3	服务器	心跳回应	有效负载为直播间人气值
     *                      5	服务器	通知	有效负载为礼物、弹幕、公告等
     *                      7	客户端	认证（加入房间）	客户端成功建立连接后发送的第一个数据包
     *                      // {"uid":0,"roomid":1938890,"protover":2,"platform":"web","clientver":"1.10.3","type":2,"key":"I1xv9faLSt4UN3_lBqJG_PxZulDTkw1uZHi-wWts0rae3a7SVlyDRm_9g4fFz16BgoOB0Ib-mnXjb32u1nAVmQMOlkbBJvE1XM1C10vGvbePbiwTMQ=="}
     *                      8	服务器	认证成功回应	服务器接受认证包后回应的第一个数据包
     *                      (4)
     *                  ]
     *                  备用字段 [ 固定为 0 (4) ]
     *              )
     *         + 消息内容
     */
    private List<Source> messageDecoder(byte[] data) {
        int index = 0;
        List<Source> sources = new ArrayList<>();
        while (true) {
            if (data.length - index >= 16) {

                byte[] len1Bytes = new byte[]{data[index++], data[index++], data[index++], data[index++]};
                int len1 = P6eToolCommon.bytesToIntBig(len1Bytes);

                byte[] len2Bytes = new byte[]{data[index++], data[index++]};
                int len2 = len2Bytes[0] + len2Bytes[1];

                byte[] agreementBytes = new byte[]{data[index++], data[index++]};
                int agreement = agreementBytes[0] + agreementBytes[1];

                byte[] typeBytes = new byte[]{data[index++], data[index++], data[index++], data[index++]};
                int type = P6eToolCommon.bytesToIntBig(typeBytes);

                byte[] spareBytes = new byte[]{data[index++], data[index++], data[index++], data[index++]};
                int spare = P6eToolCommon.bytesToIntBig(spareBytes);

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
        private byte[] bytes;
        private int type;
        private String content;

        Source(byte[] bytes, int type, String content) {
            this.bytes = bytes;
            this.type = type;
            this.content = content;
        }
    }
}
