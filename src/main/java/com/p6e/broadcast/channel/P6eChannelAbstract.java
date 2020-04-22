package com.p6e.broadcast.channel;

import com.p6e.netty.websocket.client.P6eWebsocketClientApplication;
import com.p6e.netty.websocket.client.mould.P6eNioMould;

public abstract class P6eChannelAbstract {

    /**
     * 获取 WebSocket 客户端连接对象应用
     * 全局只有一个这样的对象
     */
    protected static final P6eWebsocketClientApplication
            clientApplication = P6eWebsocketClientApplication.run(P6eNioMould.class);

    static {
        /*
         * 全局初始化
         * 初始化时间回调触发器
         */
        P6eChannelTimeCallback.create();
    }

}
