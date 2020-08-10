package com.example.demo.Component;

import com.example.demo.common.annotation.Instruction;
import com.example.demo.common.annotation.VerifiAnnotation;
import com.example.demo.model.msgFromCQ.ReceiveMsg;
import com.example.demo.util.FileUtil;
import com.example.demo.util.InstructionUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
    // Tip:volatite会不会使Map的Key值对所有线程可见有待确认
    volatile private Map<Long, List<String>> groupMsgList = new ConcurrentHashMap<>();

    // 消息存储池
    private ThreadPoolExecutor msgStroePool;
    // 缓冲区阈值
    private int msgListSize = 10;

    /**
     * 当这个实例被初次注入时所做的事情
     * 这里是将这个组件的线程池初始化
     */
    @PostConstruct
    private void initThisComponent() {
        msgStroePool = new ThreadPoolExecutor(
                1,  // 核心线程池大小（没加几个群，用不了多少核心线程）
                8,  // 最大线程池大小
                1, TimeUnit.MINUTES,    // 阻塞队列的生存时间
                new ArrayBlockingQueue<Runnable>(15),   // 阻塞队列长度
                new ThreadPoolExecutor.DiscardPolicy()    // 拒绝策略：什么也不做
        );
    }

    @VerifiAnnotation
    @Instruction(name = "setMsgListSize", descrption = "设置消息缓存大小",
                errorMsg = "设置错误，指令的参数格式为:\n消息列表大小=【数字】")
    public String setMsgListSize(ReceiveMsg receiveMsg, Map<String, String> params) {
        String resultMsg = "设置成功";
        Integer setValue = InstructionUtils.getParamValueOfInteger(params, "msgListSize", "大小");
        this.msgListSize = setValue;
        return resultMsg;
    }

    /**
     * 消息存储的线程类
     */
    private class msgStoreThread implements Runnable {
        private long groupId;
        private String[] msgList;

        public msgStoreThread(long groupId, String[] msgList) {
            this.groupId = groupId;
            this.msgList = msgList;
        }

        /**
         * 消息存储线程的方法
         * 主要干的就是组装存储数据，然后使用工具类FileUtil进行存储
         */
        @Override
        public void run() {
            StringBuffer toWirteString = new StringBuffer();
            for (String msg : msgList) {
                toWirteString.append(msg + "\r\n");
            }
            FileUtil.saveMsgToFile(groupId + ".txt", toWirteString.toString());
        }
    }

    /**
     * 存储群聊信息
     *
     * @param msg
     * @param groupId
     */
    public void saveGroupMsg(String msg, long groupId) {
        // 获取该群的群消息列表
        List<String> msgList = groupMsgList.get(groupId);
        if (msgList == null) {
            msgList = new LinkedList<>();
            groupMsgList.put(groupId, msgList);
        }
        msgList.add(msg);

        // 当消息列表长度超过阈值，将消息列表转发给子线程进行存储
        if (msgList.size() >= msgListSize) {
            String[] msgs = msgList.toArray(new String[0]);
            msgList.clear();
            msgStoreThread toStoreThrea = new msgStoreThread(groupId, msgs);
            msgStroePool.execute(toStoreThrea);
        }
    }
}
