package com.ex.erp.tool;

import org.slf4j.LoggerFactory;
import org.slf4j.spi.LocationAwareLogger;

import java.text.MessageFormat;

public class LogFactory {
    private final Class<?> clazz;
    private final LocationAwareLogger log;

    public LogFactory(Class<?> clazz) {
        this.log = (LocationAwareLogger) LoggerFactory.getLogger(clazz);
        this.clazz = clazz;
    }

    public void debug(String message, Object... args){
        log.log(null, clazz.getName(), LocationAwareLogger.DEBUG_INT, format(message, args), null, null);
    }

    public void info(String message, Object... args){
        log.log(null, clazz.getName(), LocationAwareLogger.INFO_INT, format(message, args), null, null);
    }

    public void warn(String message, Object... args){
        log.log(null, clazz.getName(), LocationAwareLogger.WARN_INT, format(message, args), null, null);
    }

    public void error(String message, Object... args){
        log.log(null, clazz.getName(), LocationAwareLogger.ERROR_INT, format(message, args), null, null);
    }

    public void error(Throwable e, String message, Object... args){
        log.log(null, clazz.getName(), LocationAwareLogger.ERROR_INT, format(message, args), null, e);
    }

    public void error(Throwable e){
        log.log(null, clazz.getName(), LocationAwareLogger.ERROR_INT, e.getMessage(), null, e);
    }
    private static String format(String message, Object... args){
        if (args != null && args.length != 0) {
            return MessageFormat.format(message, args);
        }
        return message;
    }
}
