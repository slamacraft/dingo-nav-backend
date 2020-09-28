package com.dingdo.component.otherComponent;

import com.dingdo.common.annotation.Instruction;
import com.dingdo.common.annotation.VerifiAnnotation;
import com.dingdo.enums.VerificationEnum;
import com.dingdo.msgHandler.model.ReqMsg;
import com.dingdo.util.FileUtils;
import com.dingdo.util.InstructionUtils;
import com.forte.qqrobot.anno.Async;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * 聊天消息存储组件件
 */
@Component
public class SaveMsgComponent {

    private final static Logger logger = Logger.getLogger(SaveMsgComponent.class);

    // 群消息列表(不一定线程安全)
    private final Map<String, StringBuilder> msgMap = new HashMap<>();

    // 缓冲区阈值
    private int bufferSize = 100;


    @VerifiAnnotation(level = VerificationEnum.DEVELOPER)
    @Instruction(description = "设置消息缓存", errorMsg = "参数格式: 缓存大小=[数字 <= INT.MAX]")
    public String setMsgListSize(ReqMsg reqMsg, Map<String, String> params) {
        this.bufferSize = InstructionUtils.getParamValueOfInteger(params, "bufferSize", "缓存大小");
        return "设置成功";
    }


    /**
     * 存储群聊消息的方法
     * <p>
     * 本方法会为每个群聊都赋予一个消息缓冲队列，当缓冲区{@code msgListSize}
     * 满后，会将消息异步存储到对应的文件
     * </p>
     *
     * @param msg
     * @param groupId
     */
    public void saveGroupMsg(String msg, String groupId) {
        appendMsg(msg, groupId);
        StringBuilder msgString = msgMap.get(groupId);
        if (msgString.length() >= bufferSize) {
            writeMsgToFile(msgString.toString(), groupId);
        }
    }

    public void appendMsg(String msg, String groupId) {
        StringBuilder msgBuilder = msgMap.get(groupId);
        if (msgBuilder == null) {
            msgBuilder = new StringBuilder();
            msgMap.put(groupId, msgBuilder);
        }

        msgBuilder.append(msg).append("\r\n");
    }


    @Async
    public void writeMsgToFile(String groupMsg, String groupId) {
        LocalDate today = LocalDate.now();
        logger.info("已储存群" + groupId + "的消息");
        FileUtils.appendTextRelativeToJar("/message/" + groupId + " " + today.toString() + ".txt", groupMsg);
    }
}
