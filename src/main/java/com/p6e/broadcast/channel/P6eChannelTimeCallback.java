package com.p6e.broadcast.channel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 自定义的时间回调器
 */
public class P6eChannelTimeCallback {

    /**
     * 时间回调器的执行器回调函数
     */
    public interface Actuator {
        public void execute(Config config) throws Exception;
    }

    /**
     * 时间回调器的配置信息
     */
    public static class Config {
        private long wait = 1; // 等候时间, 单位秒
        private boolean promptly = false; // 是否迅速执行
        private boolean interval = false; // 是否开启间隔
        private Actuator actuator; // 回调函数执行器

        public Config(long wait, Actuator actuator) {
            this(wait, true, actuator);
        }

        public Config(long wait, boolean interval, Actuator actuator) {
            this(wait, false, interval, actuator);
        }

        public Config(long wait, boolean promptly, boolean interval, Actuator actuator) {
            this.setWait(wait);
            this.setPromptly(promptly);
            this.setInterval(interval);
            this.setActuator(actuator);
        }

        // 系统内部计数对象
        private long __counter__ = 0;

        public long getWait() {
            return wait;
        }

        public void setWait(long wait) {
            this.wait = wait;
            this.initializeCounter();
        }

        public boolean isInterval() {
            return interval;
        }

        public void setInterval(boolean interval) {
            this.interval = interval;
        }

        public Actuator getActuator() {
            return actuator;
        }

        public void setActuator(Actuator actuator) {
            this.actuator = actuator;
        }

        public boolean isPromptly() {
            return promptly;
        }

        public void setPromptly(boolean promptly) {
            this.promptly = promptly;
        }

        @Override
        public String toString() {
            return "{" + "\"wait\":" +
                    wait +
                    ",\"promptly\":" +
                    promptly +
                    ",\"interval\":" +
                    interval +
                    ",\"actuator\":" +
                    actuator +
                    ",\"__counter__\":" +
                    __counter__ +
                    '}';
        }

        /**
         * 计时器初始化
         */
        private void initializeCounter() {
            this.__counter__ = this.getWait() * 1000;
        }

        /**
         * 计时器是否归零
         */
        private boolean isZeroingCounter() {
            return this.__counter__ <= 0;
        }

        /**
         * 时间累减
         * @param interval 减去的系统时间数量
         */
        private void reduceCounter(long interval) {
            this.__counter__ = this.__counter__ - interval;
        }
    }


    // 轮训触发回调事件的线程
    private static Thread thread;

    // 轮训间隔时间
    private static long trainingInterval = 1000;

    // 配置文件缓存的集合
    private static final List<Config> configs = new ArrayList<>();

    // 删除对象的集合
    private static final ConcurrentLinkedQueue<Config> queue = new ConcurrentLinkedQueue<>();

    /**
     * 创建一个时间触发器，全局也只有一个
     */
    public static void create() {
        create(1000);
    }

    /**
     * 创建一个时间触发器，全局也只有一个
     * @param ti 时间触发器轮训的间隔时间
     */
    public static void create(long ti) {
        // 注入间隔时间
        trainingInterval = ti;
        if (thread == null) {
            thread = new Thread() {
                @Override
                @SuppressWarnings("InfiniteLoopStatement")
                public void run() {
                    super.run();
                    while (true) {
                        try {
                            // 每个一段时间遍历一次计数器
                            Iterator<Config> iterator = configs.iterator();
                            while (iterator.hasNext()) {
                                Config config = iterator.next();
                                try {
                                    if (config == null || config.getActuator() == null) iterator.remove();
                                    else {
                                        // 是否执行回调函数
                                        boolean bool;
                                        // 是否立即执行
                                        if (config.isPromptly()) {
                                            bool = true;
                                            config.setPromptly(false);
                                        } else {
                                            // 累减
                                            config.reduceCounter(trainingInterval);
                                            // 是否归零了计数器
                                            bool = config.isZeroingCounter();
                                        }
                                        if (bool) {
                                            if (config.isInterval()) config.initializeCounter();
                                            else iterator.remove(); // 删除加入的时间回调器
                                            config.getActuator().execute(config);
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    configs.remove(config); // 删除加入的时间回调器
                                }
                            }
                            // 休眠时间为间隔时间
                            Thread.sleep(trainingInterval);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
            thread.start();
        }
    }

    /**
     * 添加一个事件轮训触发器
     * @param config 配置文件
     */
    public static void addConfig(Config config) {
        if (thread != null) configs.add(config);
        else throw new RuntimeException("Rotation thread not created.");
    }

    /**
     * 删除一个事件触发器
     * @param config 配置文件
     */
    public static void removConfig(Config config) {
        queue.offer(config);
    }
}
