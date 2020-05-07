package com.p6e.broadcast.common;


import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;

/**
 * 加载日志的工具类
 * @version 1.0
 */
public class P6eLoggerCommon {

    /** 日志注入对象 */
    private static final Logger logger = LoggerFactory.getLogger(P6eLoggerCommon.class);

    /** 是否加载过 logback 配置信息 */
    private static volatile boolean bool = false;

    private static LoggerContext loggerContext;

    /** 加载 logback 配置信息 */
    public synchronized static void init() {
        if (!bool) {
            try {
                URL filePath = P6eLoggerCommon.class.getClassLoader().getResource("./logback.xml");
                if (filePath == null) throw new NullPointerException("P6eLoggerCommon filePath");
                loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
                JoranConfigurator configurator = new JoranConfigurator();
                configurator.setContext(loggerContext);
                loggerContext.reset();
                configurator.doConfigure(filePath);
                StatusPrinter.printInCaseOfErrorsOrWarnings(loggerContext);
                bool = true;
                headerLog();
            } catch (JoranException e) {
                bool = false;
                e.printStackTrace();
            }
        }
    }

    /** 头部输出的日志 */
    private static void headerLog() {
        logger.info("    ____    _____                 ____                               __                          __ ");
        logger.info("   / __ \\  / ___/  ___           / __ )   _____  ____   ____ _  ____/ /  _____  ____ _   _____  / /_");
        logger.info("  / /_/ / / __ \\  / _ \\         / __  |  / ___/ / __ \\ / __ `/ / __  /  / ___/ / __ `/  / ___/ / __/");
        logger.info(" / ____/ / /_/ / /  __/        / /_/ /  / /    / /_/ // /_/ / / /_/ /  / /__  / /_/ /  (__  ) / /_  ");
        logger.info("/_/      \\____/  \\___/        /_____/  /_/     \\____/ \\__,_/  \\__,_/   \\___/  \\__,_/  /____/  \\__/  ");
        logger.info("                                                                                                    ");
    }

    public static void level() {
        loggerContext.getLogger("root").setLevel(Level.INFO);
        loggerContext.getLogger("root").setLevel(Level.WARN);
        loggerContext.getLogger("root").setLevel(Level.ERROR);
    }
}
