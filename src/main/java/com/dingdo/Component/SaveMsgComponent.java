package com.dingdo.Component;

import com.dingdo.common.annotation.Instruction;
import com.dingdo.common.annotation.VerifiAnnotation;
import com.dingdo.enums.VerificationEnum;
import com.dingdo.model.msgFromMirai.ReqMsg;
import com.dingdo.util.FileUtil;
import com.dingdo.util.InstructionUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 聊天消息存储组件件
 */
@Component
public class SaveMsgComponent {
    // 群消息列表(不一定线程安全)
    volatile private Map<String, List<String>> groupMsgList = new ConcurrentHashMap<>();

    // 消息存储池
    private ThreadPoolExecutor msgStorePool;
    // 缓冲区阈值
    private int msgListSize = 10;


    /**
     * 当这个实例被初次注入时所做的事情
     * 这里是将这个组件的线程池初始化
     */
    @PostConstruct
    private void initThisComponent() {
        msgStorePool = new ThreadPoolExecutor(
                1,  // 核心线程池大小（没加几个群，用不了多少核心线程）
                8,  // 最大线程池大小
                1, TimeUnit.MINUTES,    // 阻塞队列的生存时间
                new ArrayBlockingQueue<>(15),   // 阻塞队列长度
                new ThreadPoolExecutor.DiscardPolicy()    // 拒绝策略：什么也不做
        );
    }

    @VerifiAnnotation(level = VerificationEnum.DEVELOPER)
    @Instruction(description = "设置消息缓存大小",
            errorMsg = "设置错误，指令的参数格式为:\n消息列表大小=【数字】")
    public String setMsgListSize(ReqMsg reqMsg, Map<String, String> params) {
        String resultMsg = "设置成功";
        this.msgListSize = InstructionUtils.getParamValueOfInteger(params, "msgListSize", "大小");
        return resultMsg;
    }

    /**
     * 消息存储的线程类
     */
    private class msgStoreThread implements Runnable {
        private String groupId;
        private String[] msgList;

        msgStoreThread(String groupId, String[] msgList) {
            this.groupId = groupId;
            this.msgList = msgList;
        }

        /**
         * 消息存储线程的方法
         * 主要干的就是组装存储数据，然后使用工具类FileUtil进行存储
         */
        @Override
        public void run() {
            String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            String toWriteString = Arrays.stream(msgList).reduce((item1, item2) -> item1 + item2).get();
            FileUtil.saveMsgToFile(groupId + " " + today + ".txt", toWriteString);
        }
    }


    /**
     * 存储群聊信息
     *
     * @param msg
     * @param groupId
     */
    public void saveGroupMsg(String msg, String groupId) {
        // 获取该群的群消息列表
        List<String> msgList = groupMsgList.get(groupId);
        if (CollectionUtils.isEmpty(msgList)) {
            msgList = new LinkedList<>();
            groupMsgList.put(groupId, msgList);
        }
        msgList.add(msg + "\r\n");

        // 当消息列表长度超过阈值，将消息列表分发给子线程进行存储
        if (msgList.size() >= msgListSize) {
            String[] messageList = msgList.toArray(new String[0]);
            msgList.clear();
            msgStorePool.execute(new msgStoreThread(groupId, messageList));
        }
    }
}
