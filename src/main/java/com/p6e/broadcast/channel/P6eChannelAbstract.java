package com.p6e.broadcast.channel;

import com.p6e.netty.websocket.client.P6eWebsocketClientApplication;
import com.p6e.netty.websocket.client.mould.P6eNioMould;

public abstract class P6eChannelAbstract {

    protected static final P6eWebsocketClientApplication
            clientApplication = P6eWebsocketClientApplication.run(P6eNioMould.class);

    static {
        P6eChannelTimeCallback.create();
    }

}
