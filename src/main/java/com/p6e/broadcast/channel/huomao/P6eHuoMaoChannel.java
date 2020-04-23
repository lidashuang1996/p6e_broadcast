package com.p6e.broadcast.channel.huomao;

import com.p6e.broadcast.channel.P6eChannelAbstract;
import com.p6e.broadcast.channel.P6eChannelCallback;
import com.p6e.broadcast.channel.P6eChannelTimeCallback;
import com.p6e.netty.websocket.client.instructions.P6eInstructionsAbstractAsync;
import com.p6e.netty.websocket.client.product.P6eProduct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 火猫直播 http://www.huomao.com/
 * 火猫直播房间消息连接器
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

    public static P6eHuoMaoChannel create(String rid, P6eChannelCallback.HuoMao callback) {
        if (callback == null) throw new RuntimeException("P6eChannelCallback.HuoMao null");
        return new P6eHuoMaoChannel(rid, callback);
    }

    private P6eHuoMaoChannel(String rid, P6eChannelCallback.HuoMao callback) {
        this.rid = rid;
        this.callback = callback;
//        this.inventory = new P6eHuoMaoChannelInventory(rid);
//        this.connect();
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
                            messages.add(P6eHuoMaoChannelMessage.build(source.bytes, source.content));
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

    private byte[] messageEncoder(String message, int type) {
        return null;
    }


    private List<Source> messageDecoder(byte[] bytes) {
        return null;
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
