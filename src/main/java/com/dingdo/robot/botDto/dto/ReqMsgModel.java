package com.dingdo.robot.botDto.dto;

import com.dingdo.robot.botDto.Source;
import com.dingdo.robot.botDto.ReqMsg;

/**
 * @author slamacraft
 * @version 1.0
 * @date 2020/12/3 15:02
 * @since JDK 1.8
 */
public class ReqMsgModel implements ReqMsg {
    private String id;
    private Source source;
    private String msg;
    private Long time;
    private Object sourceMsg;

    public ReqMsgModel() {
    }


    public void setId(String id) {
        this.id = id;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public void setSource(Source source) {
        this.source = source;
    }

    public void setSourceMsg(Object sourceMsg) {
        this.sourceMsg = sourceMsg;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Source getSource() {
        return source;
    }

    @Override
    public String getMsg() {
        return msg;
    }

    @Override
    public Long getTime() {
        return time;
    }

    @Override
    public Object getSourceMsg() {
        return sourceMsg;
    }
}
