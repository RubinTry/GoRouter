package cn.gorouter.gorouter_api.logger;

import android.util.Log;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author logcat
 * @date 2020/07/11 16:29
 * The base class for logger
 */
public class BaseLogPrinter implements ILogPrinter {

    private String TAG = this.getClass().getSimpleName();

    protected String splitChar;

    private boolean open;

    protected String message;

    public BaseLogPrinter() {
        open = false;
        if(splitChar == null){
            splitChar = "*";
        }
    }

    @Override
    public void setSplitChar(String splitChar) {
        this.splitChar = splitChar;
    }

    @Override
    public void open() {
        open = true;
    }

    @Override
    public void setTag(String tag) {
        this.TAG = tag;
    }


    @Override
    public void debug(String message) {
        if (!open) {
            return;
        }
        this.message = changeCharset(message , StandardCharsets.ISO_8859_1);
        Log.d(TAG, message);
    }

    @Override
    public void debug(String message, Throwable throwable) {
        if (!open) {
            return;
        }
        this.message = changeCharset(message , StandardCharsets.ISO_8859_1);;
        Log.d(TAG, message, throwable);
    }

    @Override
    public void error(String message) {
        if (!open) {
            return;
        }
        this.message = changeCharset(message , StandardCharsets.ISO_8859_1);;
        Log.e(TAG, message);
    }

    @Override
    public void error(String message, Throwable throwable) {
        if (!open) {
            return;
        }
        this.message = changeCharset(message , StandardCharsets.ISO_8859_1);;
        Log.e(TAG, message, throwable);
    }

    @Override
    public void info(String message) {
        if (!open) {
            return;
        }
        this.message = changeCharset(message , StandardCharsets.ISO_8859_1);;
        Log.i(TAG, message);
    }

    @Override
    public void info(String message, Throwable throwable) {
        if (!open) {
            return;
        }
        this.message = changeCharset(message , StandardCharsets.ISO_8859_1);;
        Log.i(TAG, message, throwable);
    }

    @Override
    public void warn(String message) {
        if (!open) {
            return;
        }
        this.message = changeCharset(message , StandardCharsets.ISO_8859_1);;
        Log.w(TAG, message);
    }

    @Override
    public void warn(String message, Throwable throwable) {
        if (!open) {
            return;
        }
        this.message = changeCharset(message , StandardCharsets.ISO_8859_1);;
        Log.w(TAG, message, throwable);
    }

    @Override
    public void verbose(String message) {
        if (!open) {
            return;
        }
        this.message = changeCharset(message , StandardCharsets.ISO_8859_1);;
        Log.v(TAG, message);
    }

    @Override
    public void verbose(String message, Throwable throwable) {
        if (!open) {
            return;
        }
        this.message = changeCharset(message , StandardCharsets.ISO_8859_1);;
        Log.v(TAG, message, throwable);
    }


    protected void setMessage(String message) {
        this.message = changeCharset(message , StandardCharsets.ISO_8859_1);;
    }

    @Override
    public int getMessageLength() {
        return message.getBytes(StandardCharsets.UTF_8).length;
    }

    @Override
    public int getRealLength() {
        return message.getBytes(StandardCharsets.ISO_8859_1).length;
    }

    protected String changeCharset(String msg , Charset charset){
        if(msg != null){
            byte[] bs = msg.getBytes();
            return new String(bs , charset);
        }
        return null;
    }
}
