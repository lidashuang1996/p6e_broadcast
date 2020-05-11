//package com.p6e.broadcast.channel.kuaishou;
//
//import com.p6e.broadcast.channel.P6eChannelAbstract;
//import com.p6e.broadcast.channel.P6eChannelCallback;
//import com.p6e.broadcast.channel.P6eChannelTimeCallback;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
///**
// * 快手直播 https://live.kuaishou.com/
// * 快手直播房间消息连接器
// * @version 1.0
// */
//public class P6eKuaiShouChannel extends P6eChannelAbstract {
//
//    /** 日志对象注入 */
//    private final static Logger logger = LoggerFactory.getLogger(P6eKuaiShouChannel.class);
//
//    /** 快手房间 ID */
//    private String rid;
//
//    /** Web Socket 会话客户端 ID */
//    private String clientId;
//
//    /** 快手回调函数 */
//    private P6eChannelCallback.KuaiShou callback;
//
//    /** 快手配置信息 */
//    private P6eKuaiShouChannelInventory inventory;
//
//    /**
//     * 创建 P6eKuaiShouChannel 实例对象
//     * @param urlPath 房间的 URL 路径
//     * @param callback 接收消息后的回调函数
//     * @return P6eKuaiShouChannel 实例化的对象
//     */
//    public static P6eKuaiShouChannel create(String urlPath, P6eChannelCallback.KuaiShou callback) {
//        if (callback == null) throw new RuntimeException("P6eChannelCallback.KuaiShou null");
//        return new P6eKuaiShouChannel(urlPath, callback);
//    }
//
//    /**
//     * 构造方法
//     * @param urlPath 房间的 URL 路径
//     * @param callback 接收消息后的回调函数
//     */
//    private P6eKuaiShouChannel(String urlPath, P6eChannelCallback.KuaiShou callback) {
//        this.callback = callback;
//        this.inventory = new P6eKuaiShouChannelInventory(urlPath);
//        this.rid = this.inventory.getId();
//        this.connect();
//    }
//
//    @Override
//    protected void connect() {
//// 修改状态为连接中
//        this.setStatus(CONNECT_STATUS);
//        // Web Socket 连接房间信息系统
//        if (clientApplication == null) throw new RuntimeException("client application null");
//        else {
//            clientApplication.connect(
//                    this.product = new P6eProduct(inventory.getWebSocketWssUrl(), new P6eInstructionsAbstractAsync() {
//
//                        /** 时间回调触发器的配置信息 */
//                        private P6eChannelTimeCallback.Config config;
//
//                        @Override
//                        public void onOpen(String id) {
//                            logger.debug("HuoMao onOpenAsync [ CLIENT: " + id + ", RID: " + rid + " ]");
//
//
//                            // 定时器
////                            this.config = new P6eChannelTimeCallback.Config(30, true, true,
////                                    config -> this.sendMessage(BINARY_MESSAGE_TYPE, messageEncoder(inventory.getPantInfo(), inventory.getPantType())));
////                            P6eChannelTimeCallback.addConfig(this.config);
//
//                        }
//
//                        @Override
//                        public void onClose(String id) {
//                            logger.debug("HuoMao onCloseAsync [ CLIENT: " + id + ", RID: " + rid + " ]");
//                            if (this.config != null) P6eChannelTimeCallback.removeConfig(this.config);
//                            if (!isClose()) {
//                                logger.error("HuoMao onCloseAsync [ CLIENT: " + id + ", RID: " + rid + " ]" +
//                                        " ==> retry " + incrementRetry() + ", retryCount" + getRetryCount());
//                                inventory = new P6eKuaiShouChannelInventory(rid); // 重新连接
//                                reconnect(); // 重新连接
//                            }
//                        }
//
//                        @Override
//                        public void onError(String id, Throwable throwable) {
//                            logger.error("HuoMao onErrorAsync [ CLIENT: " + id + ", RID: " + rid + " ] ==> " + throwable.getMessage());
//                        }
//
//                        @Override
//                        public void onMessageText(String id, String content) {
//                            logger.debug("HuoMao onMessageTextAsync [ CLIENT: " + id + ", RID: " + rid + " ] ==> " + content);
//                        }
//
//                        @Override
//                        public void onMessageBinary(String id, byte[] bytes) {
//
//                        }
//
//                        @Override
//                        public void onMessagePong(String id, byte[] bytes) {
//                            logger.debug("HuoMao onMessagePongAsync [ CLIENT: " + id + ", RID: " + rid + " ] ==> " + new String(bytes));
//                        }
//
//                        @Override
//                        public void onMessagePing(String id, byte[] bytes) {
//                            logger.debug("HuoMao onMessagePingAsync [ CLIENT: " + id + ", RID: " + rid + " ] ==> " + new String(bytes));
//                        }
//
//                        @Override
//                        public void onMessageContinuation(String id, byte[] bytes) {
//                            logger.debug("HuoMao onMessageContinuationAsync [ CLIENT: " + id + ", RID: " + rid + " ] ==> " + new String(bytes));
//                        }
//                    }));
//        }
//    }
//
//    @Override
//    public void close() {
//        if (clientApplication != null) {
//            this.removeCache();
//            this.setStatus(CLOSE_STATUS);
//            clientApplication.close(product.getId());
//        } else throw new RuntimeException("KuaiShou client application null");
//    }
//
//    @Override
//    protected void dump() {
//        if (clientApplication != null) {
//            this.setStatus(CLOSE_STATUS);
//            clientApplication.close(product.getId());
//        }
//    }
//}
