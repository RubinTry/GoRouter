package cn.gorouter.api.logger;

/**
 * @author logcat
 * @date 2020/07/11 16:25
 * @version 1.0.0
 */
public interface ILogPrinter {

    /**
     * set a char for split.
     * @param splitChar
     */
    void setSplitChar(String splitChar);

    /**
     * open log print , because it's default close.
     */
    void open();

    /**
     * set a tag for logger
     * @param tag
     */
    void setTag(String tag);

    /**
     * print the debug log
     * @param message The message you want to print.
     */
    void debug(String message);


    /**
     * print the debug log
     * @param message  The message you want to print.
     * @param throwable  The exception witch logger throws.
     */
    void debug(String message , Throwable throwable);


    /**
     * print the error log
     * @param message The error message you want to print.
     */
    void error(String message);


    /**
     * print the error log
     * @param message The error message you want to print.
     * @param throwable The exception witch logger throws.
     */
    void error(String message , Throwable throwable);


    /**
     * print the info log
     * @param message The info message you want to print.
     */
    void info(String message);


    /**
     * print the info log
     * @param message  The info message you want to print.
     * @param throwable The exception witch logger throws.
     */
    void info(String message , Throwable throwable);


    /**
     * print the warn log
     * @param message  The warn message you want to print.
     */
    void warn(String message);


    /**
     *  print the warn log
     * @param message The warn message you want to print.
     * @param throwable The exception witch logger throws.
     */
    void warn(String message , Throwable throwable);


    /**
     * print the verbose log
     * @param message  The verbose message you want to print.
     */
    void verbose(String message);

    /**
     * print the verbose log
     * @param message The verbose message you want to print.
     * @param throwable The exception witch logger throws.
     */
    void verbose(String message , Throwable throwable);


    /**
     * get the message's length.
     * @return
     */
    int getMessageLength();


    /**
     * get the message's real length.
     * @return
     */
    int getRealLength();
}
