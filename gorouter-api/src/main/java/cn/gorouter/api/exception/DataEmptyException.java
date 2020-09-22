package cn.gorouter.api.exception;

import androidx.annotation.Nullable;

/**
 * @author logcat
 * @date 2020/09/22
 */
public class DataEmptyException extends Exception {
    public DataEmptyException(String message) {
        super(message);
    }
}
