package ru.gbax.xmlpe.common.utils;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import java.io.File;


/**
 * Утилита для конфигурирования Log4j
 *
 * Created by GBAX on 27.07.2015.
 */
public class Log4jConfigUtil {

    final static Logger logger = Logger.getLogger(Log4jConfigUtil.class);

    public static void config() {
        logger.info("Log4j file appender configurating");
        FileAppender fa = new FileAppender();
        fa.setName("FileLogger");
        String fileName = String.format("%s%s%s.log",
                PropertiesUtils.getProperty("log.path"),
                File.separator,
                PropertiesUtils.getProperty("log.file.name"));
        fa.setFile(fileName);
        fa.setLayout(new PatternLayout("%d{yyyy-MM-dd HH:mm:ss} %-5p %c{1}:%L - %m%n"));
        fa.setThreshold(Level.DEBUG);
        fa.setAppend(true);
        fa.activateOptions();
        Logger.getRootLogger().addAppender(fa);
        logger.info("Log4j file appender successfully configured");
    }

}
