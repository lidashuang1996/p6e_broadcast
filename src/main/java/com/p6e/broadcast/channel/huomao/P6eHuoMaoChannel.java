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
 * @author LiDaShuang
 */
public class P6eHuoMaoChannel extends P6eChannelAbstract {


    // 日志对象
    private final static Logger logger = LoggerFactory.getLogger(P6eHuoMaoChannel.class);

    private int status = 0;
    private int error = 0;
    private int errorCount = 3;
    private P6eProduct product;

    // 房间 ID
    private String rid;
    // 回调函数
    private P6eChannelCallback.HuoMao callback;
    // 斗鱼配置信息
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
     * 连接火猫的房间服务器
     */
    private void connect() {
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

                        this.config = new P6eChannelTimeCallback.Config(30, true, true,
                                config -> this.sendMessage(BINARY_MESSAGE_TYPE, messageEncoder(inventory.getPantInfo(), inventory.getPantType())));
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
                        List<P6eHuoMaoChannelMessage> messages = new ArrayList<>();
                        for (Source source : sources) {
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

    /**
     * 火猫发送消息
     * 火猫消息 = 消息头部
     *              (
     *                  消息总长度 [ 小端模式转换 4]
     *                  消息头部总长度 [ 小端模式转换 2 ]
     *                  消息协议 [ 小端模式转换 2 ] 1 提交数据服务器返回的类型 0 服务器推送的数据
     *                  消息类型 [ 小端模式转换 4 ] {
     *                      7. 登录提交类型
     *                      8. 登录返回类型
     *                      2. 心跳数据提交类型
     *                      3. 心跳服务器返回的类型
     *                      5. 服务端推送的消息类型
     *                  }
     *                  消息XXX [ 小端模式转换 4 ] ( 同消息协议数据 )
     *             )
     *         + 消息内容
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
     * 火猫发送消息
     * 火猫消息 = 消息头部
     *              (
     *                  消息总长度 [ da端模式转换 4]
     *                  消息头部总长度 [ 小端模式转换 2 ]
     *                  消息协议 [ 小端模式转换 2 ] 1 提交数据服务器返回的类型 0 服务器推送的数据
     *                  消息类型 [ 小端模式转换 4 ] {
     *                      7. 登录提交类型
     *                      8. 登录返回类型
     *                      2. 心跳数据提交类型
     *                      3. 心跳服务器返回的类型
     *                      5. 服务端推送的消息类型
     *                  }
     *                  消息XXX [ 小端模式转换 4 ] ( 同消息协议数据 )
     *             )
     *         + 消息内容
     */
    private List<Source> messageDecoder(byte[] data) {
        int index = 0;
        List<Source> result = new ArrayList<>();
        while (true) {
            if (data.length - index > 4) {
                byte[] lenBytes = new byte[] { data[index++], data[index++], data[index++], data[index++] };
                int len = P6eToolCommon.bytesToIntBig(lenBytes);
                if (data.length >= (index + len - 4)) {

                    byte[] contentBytes1 = new byte[] { data[index++], data[index++] };
                    int content1 = contentBytes1[0] + contentBytes1[1];

                    byte[] contentBytes2 = new byte[] { data[index++], data[index++] };
                    int content2 = contentBytes2[0] + contentBytes2[1];

                    byte[] contentBytes3 = new byte[] { data[index++], data[index++], data[index++], data[index++] };
                    int content3 = P6eToolCommon.bytesToIntBig(contentBytes3);

                    byte[] contentBytes4 = new byte[] { data[index++], data[index++], data[index++], data[index++] };
                    int content4 = P6eToolCommon.bytesToIntBig(contentBytes4);

                    byte[] contentBytes = new byte[len];

                    P6eToolCommon.arrayJoinByte(contentBytes, lenBytes, contentBytes1, contentBytes2, contentBytes3, contentBytes4);

                    for (int _index = index; index < _index + len - 16; index++) {
                        contentBytes[index - _index + 16] = data[index];
                    }

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
