package com.p6e.broadcast.channel.lognzhu;

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
 * 龙珠直播 http://www.longzhu.com/
 * 龙珠直播房间消息连接器
 * @version 1.0
 */
public class P6eLongZhuChannel extends P6eChannelAbstract {

    /** 日志对象注入 */
    private final static Logger logger = LoggerFactory.getLogger(P6eLongZhuChannel.class);

    /** Web Socket 客户端对象 */
    private P6eProduct product;

    /** 龙珠房间 ID */
    private String rid;

    /** 龙珠回调函数 */
    private P6eChannelCallback.LongZhu callback;

    /** 龙珠配置信息 */
    private P6eLongZhuChannelInventory inventory;

    /**
     * 创建 P6eHuoMaoChannel 实例对象
     * @param urlPath 房间的 URL 路径
     * @param callback 接收消息后的回调函数
     * @return P6eLongZhuChannel 实例化的对象
     */
    public static P6eLongZhuChannel create(String urlPath, P6eChannelCallback.LongZhu callback) {
        if (callback == null) throw new RuntimeException("P6eChannelCallback.LongZhu null");
        return new P6eLongZhuChannel(urlPath, callback);
    }

    /**
     * 创建 P6eHuoMaoChannel 实例对象
     * @param rid 房间 ID
     * @param callback 接收消息后的回调函数
     * @return P6eLongZhuChannel 实例化的对象
     */
    public static P6eLongZhuChannel create(int rid, P6eChannelCallback.LongZhu callback) {
        if (callback == null) throw new RuntimeException("P6eChannelCallback.LongZhu null");
        return new P6eLongZhuChannel(rid, callback);
    }

    /**
     * 构造方法
     * @param rid 房间 ID
     * @param callback 接收消息后的回调函数
     */
    private P6eLongZhuChannel(int rid, P6eChannelCallback.LongZhu callback) {
        this.rid = String.valueOf(rid);
        this.callback = callback;
        this.inventory = new P6eLongZhuChannelInventory(rid);
        this.connect();
    }

    /**
     * 构造方法
     * @param urlPath 房间的 URL 路径
     * @param callback 接收消息后的回调函数
     */
    private P6eLongZhuChannel(String urlPath, P6eChannelCallback.LongZhu callback) {
        this.callback = callback;
        this.inventory = new P6eLongZhuChannelInventory(urlPath);
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
                    this.product = new P6eProduct(inventory.getWebSocketWsUrl(), new P6eInstructionsAbstractAsync() {

                        /** 是否初始化 */
                        private boolean bool = false;

                        /** 时间回调触发器的配置信息 */
                        private P6eChannelTimeCallback.Config config;

                        /** MID */
                        private String mid = "";

                        /** 时间戳 */
                        private long maxDateTime = 0;

                        @Override
                        public void onOpenAsync(String id) {
                            logger.debug("LongZhu onOpenAsync [ CLIENT: " + id + ", RID: " + rid + " ]");
                            config = new P6eChannelTimeCallback.Config(20, true, config ->
                                    this.sendMessage(TEXT_MESSAGE_TYPE, inventory.getPantInfo()));
                            P6eChannelTimeCallback.addConfig(config);
                        }

                        @Override
                        public void onCloseAsync(String id) {
                            logger.debug("LongZhu onCloseAsync [ CLIENT: " + id + ", RID: " + rid + " ]");
                            if (this.config != null) P6eChannelTimeCallback.removeConfig(this.config);
                            if (!isClose()) {
                                logger.error("LongZhu onCloseAsync [ CLIENT: " + id + ", RID: " + rid + " ]" +
                                        " ==> retry " + incrementRetry() + ", retryCount" + getRetryCount());
                                reconnect(); // 重新连接
                            }
                        }

                        @Override
                        public void onErrorAsync(String id, Throwable throwable) {
                            logger.error("LongZhu onErrorAsync [ CLIENT: " + id + ", RID: " + rid + " ] ==> " + throwable.getMessage());
                        }

                        @Override
                        public void onMessageTextAsync(String id, String content) {
                            if (!bool) { // 收到消息就表示连接成功了
                                bool = true;
                                resetReconnect();
                            }
                            if ("\"{}\"".equals(content)) content = "{}"; // 替换非JSON格式的字符串
                            P6eLongZhuChannelMessage message = P6eLongZhuChannelMessage.build(content);
                            if (message != null && message.getMsgId() != null) {
                                // 缓存里面查看是否存在消息
                                if (message.getAppId() != null && message.getTimestamp() > maxDateTime) {
                                    this.mid = message.getMsgId();
                                    this.maxDateTime = message.getTimestamp();
                                    List<P6eLongZhuChannelMessage> messages = new ArrayList<>();
                                    messages.add(message);
                                    callback.execute(messages);
                                } else if (message.getAppId() != null) {
                                    this.sendMessage(TEXT_MESSAGE_TYPE, inventory.getMidInfo(mid));
                                }
                            }
                        }

                        @Override
                        public void onMessageBinaryAsync(String id, byte[] bytes) {
                            logger.debug("LongZhu onMessageBinaryAsync [ CLIENT: " + id + ", RID: " + rid + " ] ==> " + new String(bytes));
                        }

                        @Override
                        public void onMessagePongAsync(String id, byte[] bytes) {
                            logger.debug("LongZhu onMessagePongAsync [ CLIENT: " + id + ", RID: " + rid + " ] ==> " + new String(bytes));
                        }

                        @Override
                        public void onMessagePingAsync(String id, byte[] bytes) {
                            logger.debug("LongZhu onMessagePingAsync [ CLIENT: " + id + ", RID: " + rid + " ] ==> " + new String(bytes));
                        }

                        @Override
                        public void onMessageContinuationAsync(String id, byte[] bytes) {
                            logger.debug("LongZhu onMessageContinuationAsync [ CLIENT: " + id + ", RID: " + rid + " ] ==> " + new String(bytes));
                        }
                    }));
        }
    }

    /**
     * 关闭龙珠的房间信息服务器
     */
    @Override
    public void close() {
        if (clientApplication != null) {
            this.removeCache();
            this.setStatus(CLOSE_STATUS);
            clientApplication.close(product.getId());
        } else throw new RuntimeException("LongZhu client application null");
    }

    @Override
    protected void dump() {
        if (clientApplication != null) {
            this.setStatus(CLOSE_STATUS);
            clientApplication.close(product.getId());
        }
    }
}
