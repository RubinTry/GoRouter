package cn.gorouter.api.logger;

/**
 * @author logcat
 */
public class LogPrinter extends BaseLogPrinter {

    private static volatile ILogPrinter logger;



    private LogPrinter() {
        setTag(this.getClass().getSimpleName());
        setSplitChar("*");
    }

    public LogPrinter(String tagName) {
        this(tagName , null);
    }

    public LogPrinter(String tagName , String splitChar){
        if(tagName != null){
            setTag(tagName);
        }

        if(splitChar != null){
            setSplitChar(splitChar);
        }
    }

    @Override
    public void setSplitChar(String splitChar) {
        super.setSplitChar(splitChar);
    }

    @Override
    public void setTag(String tag) {
        super.setTag(tag);
    }

    @Override
    public void debug(String message) {
        setMessage(message);

        StringBuilder messageBuilder = new StringBuilder();
        int length = getMessageLength();

        for (int i = 0; i < length + 8; i++) {
            messageBuilder.append(splitChar);
        }

        super.debug(messageBuilder.toString());
        messageBuilder.delete(0, messageBuilder.length());
        messageBuilder.append(splitChar)
                .append(" ")
                .append(" ")
                .append(" ")
                .append(message)
                .append(" ")
                .append(" ")
                .append(" ")
                .append(splitChar);
        super.debug(messageBuilder.toString());
        messageBuilder.delete(0, messageBuilder.length());
        for (int i = 0; i < length + 8; i++) {
            messageBuilder.append(splitChar);
        }
        super.debug(messageBuilder.toString());
    }

    @Override
    public void debug(String message, Throwable throwable) {
        setMessage(message);

        StringBuilder messageBuilder = new StringBuilder();
        int length = getMessageLength();

        for (int i = 0; i < length + 8; i++) {
            messageBuilder.append(splitChar);
        }

        super.debug(messageBuilder.toString(), throwable);
        messageBuilder.delete(0, messageBuilder.length());
        messageBuilder.append(splitChar)
                .append(" ")
                .append(" ")
                .append(" ")
                .append(message)
                .append(" ")
                .append(" ")
                .append(" ")
                .append(splitChar);
        super.debug(messageBuilder.toString(), throwable);
        messageBuilder.delete(0, messageBuilder.length());
        for (int i = 0; i < length + 8; i++) {
            messageBuilder.append(splitChar);
        }
        super.debug(messageBuilder.toString(), throwable);
    }

    @Override
    public void error(String message) {
        setMessage(message);

        StringBuilder messageBuilder = new StringBuilder();
        int length = getMessageLength();

        for (int i = 0; i < length + 8; i++) {
            messageBuilder.append(splitChar);
        }

        super.error(messageBuilder.toString());
        messageBuilder.delete(0, messageBuilder.length());
        messageBuilder.append(splitChar)
                .append(" ")
                .append(" ")
                .append(" ")
                .append(message)
                .append(" ")
                .append(" ")
                .append(" ")
                .append(splitChar);
        super.error(messageBuilder.toString());
        messageBuilder.delete(0, messageBuilder.length());
        for (int i = 0; i < length + 8; i++) {
            messageBuilder.append(splitChar);
        }
        super.error(messageBuilder.toString());
    }

    @Override
    public void error(String message, Throwable throwable) {
        setMessage(message);

        StringBuilder messageBuilder = new StringBuilder();
        int length = getMessageLength();

        for (int i = 0; i < length + 8; i++) {
            messageBuilder.append(splitChar);
        }

        super.error(messageBuilder.toString(), throwable);
        messageBuilder.delete(0, messageBuilder.length());
        messageBuilder.append(splitChar)
                .append(" ")
                .append(" ")
                .append(" ")
                .append(message)
                .append(" ")
                .append(" ")
                .append(" ")
                .append(splitChar);
        super.error(messageBuilder.toString(), throwable);
        messageBuilder.delete(0, messageBuilder.length());
        for (int i = 0; i < length + 8; i++) {
            messageBuilder.append(splitChar);
        }
        super.error(messageBuilder.toString(), throwable);
    }

    @Override
    public void info(String message) {
        setMessage(message);

        StringBuilder messageBuilder = new StringBuilder();
        int length = getMessageLength();

        for (int i = 0; i < length + 8; i++) {
            messageBuilder.append(splitChar);
        }

        super.info(messageBuilder.toString());
        messageBuilder.delete(0, messageBuilder.length());
        messageBuilder.append(splitChar)
                .append(" ")
                .append(" ")
                .append(" ")
                .append(message)
                .append(" ")
                .append(" ")
                .append(" ")
                .append(splitChar);
        super.info(messageBuilder.toString());
        messageBuilder.delete(0, messageBuilder.length());
        for (int i = 0; i < length + 8; i++) {
            messageBuilder.append(splitChar);
        }
        super.info(messageBuilder.toString());
    }

    @Override
    public void info(String message, Throwable throwable) {
        setMessage(message);

        StringBuilder messageBuilder = new StringBuilder();
        int length = getMessageLength();

        for (int i = 0; i < length + 8; i++) {
            messageBuilder.append(splitChar);
        }

        super.info(messageBuilder.toString(), throwable);
        messageBuilder.delete(0, messageBuilder.length());
        messageBuilder.append(splitChar)
                .append(" ")
                .append(" ")
                .append(" ")
                .append(message)
                .append(" ")
                .append(" ")
                .append(" ")
                .append(splitChar);
        super.info(messageBuilder.toString(), throwable);
        messageBuilder.delete(0, messageBuilder.length());
        for (int i = 0; i < length + 8; i++) {
            messageBuilder.append(splitChar);
        }
        super.info(messageBuilder.toString(), throwable);
    }

    @Override
    public void warn(String message) {
        setMessage(message);

        StringBuilder messageBuilder = new StringBuilder();
        int length = getMessageLength();

        for (int i = 0; i < length + 8; i++) {
            messageBuilder.append(splitChar);
        }

        super.warn(messageBuilder.toString());
        messageBuilder.delete(0, messageBuilder.length());
        messageBuilder.append(splitChar)
                .append(" ")
                .append(" ")
                .append(" ")
                .append(message)
                .append(" ")
                .append(" ")
                .append(" ")
                .append(splitChar);
        super.warn(messageBuilder.toString());
        messageBuilder.delete(0, messageBuilder.length());
        for (int i = 0; i < length + 8; i++) {
            messageBuilder.append(splitChar);
        }
        super.warn(messageBuilder.toString());
    }

    @Override
    public void warn(String message, Throwable throwable) {
        setMessage(message);

        StringBuilder messageBuilder = new StringBuilder();
        int length = getMessageLength();

        for (int i = 0; i < length + 8; i++) {
            messageBuilder.append(splitChar);
        }

        super.warn(messageBuilder.toString(), throwable);
        messageBuilder.delete(0, messageBuilder.length());
        messageBuilder.append(splitChar)
                .append(" ")
                .append(" ")
                .append(" ")
                .append(message)
                .append(" ")
                .append(" ")
                .append(" ")
                .append(splitChar);
        super.warn(messageBuilder.toString(), throwable);
        messageBuilder.delete(0, messageBuilder.length());
        for (int i = 0; i < length + 8; i++) {
            messageBuilder.append(splitChar);
        }
        super.warn(messageBuilder.toString(), throwable);
    }

    @Override
    public void verbose(String message) {
        setMessage(message);

        StringBuilder messageBuilder = new StringBuilder();
        int length = getMessageLength();

        for (int i = 0; i < length + 8; i++) {
            messageBuilder.append(splitChar);
        }

        super.verbose(messageBuilder.toString());
        messageBuilder.delete(0, messageBuilder.length());
        messageBuilder.append(splitChar)
                .append(" ")
                .append(" ")
                .append(" ")
                .append(message)
                .append(" ")
                .append(" ")
                .append(" ")
                .append(splitChar);
        super.verbose(messageBuilder.toString());
        messageBuilder.delete(0, messageBuilder.length());
        for (int i = 0; i < length + 8; i++) {
            messageBuilder.append(splitChar);
        }
        super.verbose(messageBuilder.toString());
    }

    @Override
    public void verbose(String message, Throwable throwable) {
        setMessage(message);

        StringBuilder messageBuilder = new StringBuilder();
        int length = getMessageLength();

        for (int i = 0; i < length + 8; i++) {
            messageBuilder.append(splitChar);
        }

        super.verbose(messageBuilder.toString(), throwable);
        messageBuilder.delete(0, messageBuilder.length());
        messageBuilder.append(splitChar)
                .append(" ")
                .append(" ")
                .append(" ")
                .append(message)
                .append(" ")
                .append(" ")
                .append(" ")
                .append(splitChar);
        super.verbose(messageBuilder.toString(), throwable);
        messageBuilder.delete(0, messageBuilder.length());
        for (int i = 0; i < length + 8; i++) {
            messageBuilder.append(splitChar);
        }
        super.verbose(messageBuilder.toString(), throwable);
    }
}
