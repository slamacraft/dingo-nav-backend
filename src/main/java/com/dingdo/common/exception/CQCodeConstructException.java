package com.dingdo.common.exception;

/**
 * 一些声明信息
 *
 * @author slamacraft
 * @Description:
 * @date: 2020/8/31 14:24
 * @since JDK 1.8
 */
public class CQCodeConstructException extends Exception {

    private static final long serialVersionUID = 1L;

    private String message;
    private int code = 500;

    public CQCodeConstructException(String message) {
        super(message);
        this.message = message;
    }

    public CQCodeConstructException(String message, Throwable e) {
        super(message, e);
        this.message = message;
    }

    public CQCodeConstructException(String message, int code) {
        super(message);
        this.message = message;
        this.code = code;
    }

    public CQCodeConstructException(String message, int code, Throwable e) {
        super(message, e);
        this.message = message;
        this.code = code;
    }

    public String getmessage() {
        return message;
    }

    public void setmessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

}
