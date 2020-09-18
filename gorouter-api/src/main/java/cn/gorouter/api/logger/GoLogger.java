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
        if(logger.isOpen()){
            logger.debug(message);
        }
    }


    public static void debug(String message, Throwable throwable) {
        if(logger.isOpen()){
            logger.debug(message, throwable);
        }
    }


    public static void error(String message) {
        if(logger.isOpen()){
            logger.error(message);
        }
    }

    public static void error(String message, Throwable throwable) {
        if(logger.isOpen()){
            logger.error(message, throwable);
        }
    }


    public static void info(String message) {
       if(logger.isOpen()){
           logger.info(message);
       }
    }

    public static void info(String message, Throwable throwable) {
        if(logger.isOpen()){
            logger.info(message, throwable);
        }
    }

    public static void warn(String message) {
        if(logger.isOpen()){
            logger.warn(message);
        }
    }

    public static void warn(String message, Throwable throwable) {
        if(logger.isOpen()){
            logger.warn(message, throwable);
        }
    }


    public static void verbose(String message) {
        if(logger.isOpen()){
            logger.verbose(message);
        }
    }


    public static void verbose(String message, Throwable throwable) {
        if(logger.isOpen()){
            logger.verbose(message, throwable);
        }
    }


    public static boolean isOpen(){
        return logger.isOpen();
    }

}
