package com.dingo.common.exception;

/**
 * @author slamacraft
 * @version 1.0
 * @date 2020/9/18 16:16
 * @since JDK 1.8
 */
public class InstructionExecuteException extends Exception{

    private static final long serialVersionUID = 1L;

    private String message;

    public InstructionExecuteException(String message) {
        super(message);
        this.message = message;
    }

    public InstructionExecuteException(String message, Throwable e) {
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
