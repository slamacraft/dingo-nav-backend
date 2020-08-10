package com.dingdo.common.exception;

public class ClassifierInitializeException extends RuntimeException{
    private static final long serialVersionUID = 1L;

    private String message;

    public ClassifierInitializeException(String message) {
        super(message);
        this.message = message;
    }

    public ClassifierInitializeException(String message, Throwable e) {
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
