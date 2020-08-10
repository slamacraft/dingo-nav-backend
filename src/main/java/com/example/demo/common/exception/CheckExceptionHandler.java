package com.example.demo.common.exception;

import com.example.demo.model.msgFromCQ.ReplyMsg;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CheckExceptionHandler {

    @ExceptionHandler(CheckException.class)
    public ReplyMsg handleBusinessException(CheckException e) {
        ReplyMsg replyMsg = new ReplyMsg();
        replyMsg.setReply(e.getmessage());
        return replyMsg;
    }
}
