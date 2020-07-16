package cn.gorouter.api.logger;


/**
 * @author logcat
 */
public class GoLogger {

    static ILogPrinter logger = new LogPrinter(GoLogger.class.getSimpleName());


    private GoLogger() {
    }

    public static void setSplitChar(String splitChar){
        logger.setSplitChar(splitChar);
    }

    public static synchronized void openLog() {
        logger.open();
    }

    public static void debug(String message) {
        logger.debug(message);
    }


    public static void debug(String message, Throwable throwable) {
        logger.debug(message, throwable);
    }


    public static void error(String message) {
        logger.error(message);
    }

    public static void error(String message, Throwable throwable) {
        logger.error(message, throwable);
    }


    public static void info(String message) {
        logger.info(message);
    }

    public static void info(String message, Throwable throwable) {
        logger.info(message, throwable);
    }

    public static void warn(String message) {
        logger.warn(message);
    }

    public static void warn(String message, Throwable throwable) {
        logger.warn(message, throwable);
    }


    public static void verbose(String message) {
        logger.verbose(message);
    }


    public static void verbose(String message, Throwable throwable) {
        logger.verbose(message, throwable);
    }


}
