package com.dingdo.Component;

import com.dingdo.Component.componentBean.MsgBean;
import com.dingdo.service.GroupMsgService;
import com.dingdo.service.PrivateMsgService;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

@Component
@Async
public class SendMsgComponent {
    @Autowired
    private ThreadPoolExecutor msgMqPool;

    @Autowired
    private PrivateMsgService privateMsgService;

    @Autowired
    private GroupMsgService groupMsgService;

    private Queue<MsgBean> msgQueue = new LinkedBlockingQueue<>();

    volatile private List<Queue<MsgBean>> registedMsgQueue = new LinkedList<>();

    private class MsgSendTask implements Runnable {

        @SneakyThrows
        @Override
        public void run() {
            StringBuffer msgBuffer = new StringBuffer();

            while (registedMsgQueue != null && registedMsgQueue.size() > 0) {

                for (Queue<MsgBean> queue : registedMsgQueue) {
                    if (queue.size() == 0) {
                        continue;
                    }
                    msgBuffer.setLength(0);
                    MsgBean poll = queue.poll();

                    if (poll.getSourceId() != null) {
                        msgBuffer.append("来自：(" + poll.getSourceId() + ")");
                        if (StringUtils.isNotBlank(poll.getSourceNickName())) {
                            msgBuffer.append(poll.getSourceNickName());
                        }
                        if (StringUtils.isNotBlank(poll.getSourceType())) {
                            if ("private".equals(poll.getSourceType())) {
                                msgBuffer.append("私聊");
                            }
                            if ("group".equals(poll.getSourceType())) {
                                msgBuffer.append("群聊");
                            }
                        }
                        msgBuffer.append("的消息\n");
                    }

                    msgBuffer.append(poll.getMsg());
                    if ("private".equals(poll.getTargetType())) {
                        privateMsgService.sendPrivateMsg(poll.getTargetId(), msgBuffer.toString());
                    }
                    if ("group".equals(poll.getTargetType())) {
                        groupMsgService.sendGroupMsg(poll.getTargetId(), msgBuffer.toString());
                    }
                }
            }
        }
    }

    @PostConstruct
    public void sendMsg() {
        registedMsgQueue.add(msgQueue);
        MsgSendTask msgSendTask = new MsgSendTask();
        Thread thread = new Thread(msgSendTask);
        thread.start();
    }

    public void registerMsgQueue(Queue<MsgBean> msgQueue) {
        registedMsgQueue.add(msgQueue);
    }


    public Queue<MsgBean> getMsgQueue() {
        return msgQueue;
    }
}
