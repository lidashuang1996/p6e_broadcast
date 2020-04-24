package com.p6e.broadcast.channel;

import com.p6e.netty.websocket.client.P6eWebsocketClientApplication;
import com.p6e.netty.websocket.client.mould.P6eNioMould;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 弹幕服务连接器的父类
 * @version 1.0
 */
public abstract class P6eChannelAbstract {

    /** 缓存一下，子类连接器 */
    private static List<P6eChannelAbstract> cache = new ArrayList<>();

    // 删除对象的集合
    private static final ConcurrentLinkedQueue<P6eChannelAbstract> queue = new ConcurrentLinkedQueue<>();

    /**
     * 获取 WebSocket 客户端连接对象应用
     * 全局只有一个这样的对象
     */
    protected static P6eWebsocketClientApplication
            clientApplication = P6eWebsocketClientApplication.run(P6eNioMould.class);

    static {
        /*
         * 全局初始化
         * 初始化时间回调触发器
         */
        P6eChannelTimeCallback.create();
    }

    /** 当前连接器的状态 */
    protected static final int CONNECT_STATUS = 1;
    /** 当前连接器的状态 */
    protected static final int CLOSE_STATUS = -1;
    /** 当前连接器的状态 */
    protected static final int OPEN_STATUS = 2;

    /** 当前连接器的状态 0 表示没有未连接的状态 */
    private int status = 0;
    /** 当前连接器的重试次数 */
    private int retry = 0;
    /** 当前连接器的重试次数统计 */
    private int retryCount = 3;


    protected P6eChannelAbstract() {
        cache.add(this);
        System.out.println("11111111111111111111111111111111111111111111");
    }

    /**
     * 连接器连接房间的方法
     */
    protected abstract void connect();

    /**
     * 连接器关闭房间的方法且删除缓存
     */
    public abstract void close();

    /**
     * 连接器关闭房间的方法
     */
    protected abstract void dump();

    /**
     * 摧毁连接器工厂
     * 1. 关闭心跳线程
     * 2. 关闭 WebSocket 客户端
     */
    public static void destroy() {
        Iterator<P6eChannelAbstract> iterator = cache.iterator();
        while (iterator.hasNext()) {
            iterator.remove();
            iterator.next().dump();
        }
        // 关闭
        clientApplication.close();
        // 赋值为 null
        clientApplication = null;
        // 关闭心跳线程
        P6eChannelTimeCallback.destroy();
    }

    /**
     * 设置当前连接器的状态
     * @param status 状态数据
     */
    protected void setStatus(int status) {
        this.status = status;
    }

    protected boolean isClose() {
        return CLOSE_STATUS == status;
    }

    protected int incrementRetry() {
        return ++retry;
    }

    protected int getRetryCount() {
        return retryCount;
    }

    protected void reconnect() {
        if (retry <= retryCount) connect();
    }

    protected void resetReconnect() {
        retry = 0;
    }

    protected void removeCache() {
        queue.offer(this);
        P6eChannelAbstract channel = queue.poll();
        if (channel != null) {
            cache.remove(channel);
            removeCache();
        }
    }

}
