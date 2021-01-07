package com.dingdo.common.exception;

/**
 * @author slamacraft
 * @version 1.0
 * @date 2020/9/18 16:24
 * @since JDK 1.8
 */
public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private String message;

    public BusinessException(String message) {
        super(message);
        this.message = message;
    }

    public BusinessException(String message, Throwable e) {
        super(message, e);
        this.message = message;
    }

    public String getmessage() {
        return message;
    }

    public void setmessage(String message) {
        this.message = message;
    }

}
