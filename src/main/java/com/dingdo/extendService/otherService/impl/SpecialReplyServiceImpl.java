package com.dingdo.extendService.otherService.impl;

import com.dingdo.extendService.otherService.SpecialReplyService;
import com.dingdo.model.msgFromMirai.ReqMsg;
import com.dingdo.service.impl.GroupMsgServiceImpl;
import com.dingdo.util.FileUtil;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 一些声明信息
 *
 * @author slamacraft
 * @Description:
 * @date: 2020/8/20 11:21
 * @since JDK 1.8
 */
@Service
public class SpecialReplyServiceImpl implements SpecialReplyService {

    @Autowired
    private GroupMsgServiceImpl groupMsgService;

    private Map<String, String[]> groupMsgMap = new HashMap<>();

    private Map<String, RereadMsgQueue> reReadGroupMsgMap = new HashMap<>();

    private int MIN_REREAD_COUNT = 2;

    @Data
    class RereadMsgInfo {
        private String userId;
        private List<String> message = new ArrayList<>();
        private int status = 0;
        private boolean flag = false; // 是否参与复读

        public RereadMsgInfo(ReqMsg reqMsg) {
            this.userId = reqMsg.getUserId();
            this.message.add(reqMsg.getMessage());
        }
    }

    @Data
    class RereadMsgQueue {
        volatile private List<RereadMsgInfo> msgInfoList = new LinkedList<>();

        public RereadMsgQueue(ReqMsg reqMsg) {
            msgInfoList.add(new RereadMsgInfo(reqMsg));
        }
    }


    public void rereadGroupMsg(ReqMsg reqMsg) {
        RereadMsgQueue rereadMsgQueue = reReadGroupMsgMap.get(reqMsg.getGroupId() + reqMsg.getSelfId());

        if (rereadMsgQueue == null) {
            reReadGroupMsgMap.put(reqMsg.getGroupId() + reqMsg.getSelfId(), new RereadMsgQueue(reqMsg));
            return;
        }

        List<RereadMsgInfo> msgInfoList = rereadMsgQueue.getMsgInfoList();
        RereadMsgInfo currentMsgInfo = msgInfoList.get(msgInfoList.size() - 1);

        // 如果是同一个人继续发送的消息
        if (currentMsgInfo.getUserId().equals(reqMsg.getUserId())) {
            currentMsgInfo.getMessage().add(reqMsg.getMessage());
        } else {
            msgInfoList.add(new RereadMsgInfo(reqMsg));
            msgInfoList.get(0).setStatus(0);
        }

        toReread(reqMsg, rereadMsgQueue);
    }

    /**
     * @param reqMsg
     * @param rereadMsgQueue
     */
    public void toReread(ReqMsg reqMsg, RereadMsgQueue rereadMsgQueue) {
        List<RereadMsgInfo> msgInfoList = rereadMsgQueue.getMsgInfoList();

        // 最少也要2句话才算复读吧
        if (msgInfoList.size() < 2) {
            return;
        }

        RereadMsgInfo currentMsgInfo = msgInfoList.get(msgInfoList.size() - 1);// 当前用户的消息

        RereadMsgInfo startMsg = msgInfoList.get(0);
        int status = startMsg.getStatus(); // 匹配的状态

        if (status == startMsg.getMessage().size()) {
            if (currentMsgInfo.getUserId().equals(reqMsg.getUserId())) {
                // 将最新发送者作为最初发送者
                msgInfoList.clear();
                msgInfoList.add(currentMsgInfo);
            }
            return;
        }

        // 新发送的消息如果从没有匹配过任何消息，优先与队列尾进行复读匹配
        if (startMsg.getStatus() == 0) {
            // 查找第一句话在最新的发送者的消息中的位置
            int index = this.getIndexOf(startMsg.getMessage(), reqMsg.getMessage());
            if (index == 0) {   // 和第一句话匹配上，那么接下来的匹配肯定和最初发送者的相同
                status = index + 1;
                startMsg.setStatus(status);
            }
            // 和非第一句话匹配上，那么和接下来的匹配和最初发送者的不同了
            else if (index > 0) {
                // 将最新发送者作为最初发送者
                rereadMsgQueue.setMsgInfoList(msgInfoList.subList(msgInfoList.size() - 2, msgInfoList.size()));
                startMsg = rereadMsgQueue.getMsgInfoList().get(0);

                List<String> message = startMsg.getMessage();
                startMsg.setMessage(message.subList(index, message.size()));

                status += 1;
                startMsg.setStatus(status);
            } else { // 如果第一句话未能进行复读匹配
                rereadMsgQueue.setMsgInfoList(msgInfoList.subList(msgInfoList.size() - 1, msgInfoList.size()));
                return;
            }
        }
        // 如果不是第一次匹配，那么进行剩余消息的匹配
        else if (this.equals(startMsg.getMessage().get(status), reqMsg.getMessage())) {
            status += 1;
            startMsg.setStatus(status);
        }else {
            return;
        }

        // 消息全部匹配完成
        if (status == startMsg.getMessage().size()) {
            // 达到最小参与复读的次数, 且没有复读过
            if (msgInfoList.size() >= MIN_REREAD_COUNT && !startMsg.isFlag()) {
                for (String msg : startMsg.getMessage()) {
                    groupMsgService.sendGroupMsg(reqMsg.getSelfId(), reqMsg.getGroupId(), msg);
                }
                startMsg.setFlag(true);
            }
        }
    }

    private boolean equals(String msg1, String msg2) {
        if (msg1 == msg2) {
            return true;
        }
        if (msg1 == null || msg2 == null) {
            return false;
        }
        String textMsg1 = msg1.replaceAll("\\[CQ:image.*?\\]", "");
        String imageMsg1 = "";
        String textMsg2 = msg2.replaceAll("\\[CQ:image.*?\\]", "");
        String imageMsg2 = "";
        if (msg1.contains("CQ:image")) {
            imageMsg1 = msg1.split("\\[CQ:image,image=")[1].split(",")[0];
        }
        if (msg2.contains("CQ:image")) {
            imageMsg2 = msg2.split("\\[CQ:image,image=")[1].split(",")[0];
        }

        return textMsg1.equals(textMsg2) && imageMsg1.equals(imageMsg2);
    }

    private int getIndexOf(List<String> msgList, String msg) {
        for (int i = 0; i < msgList.size(); i++) {
            if (this.equals(msgList.get(i), msg)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public String getRandomGroupMsgYesterday(ReqMsg reqMsg) {

        String groupId = reqMsg.getGroupId();
        String[] msgList = groupMsgMap.get(groupId + " "+ new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        if (msgList == null || msgList.length < 3) {
            String filePath = reqMsg.getGroupId() + " " + getYesterdayDate() + ".txt";
            String msg = FileUtil.loadFileFromJarPath(filePath);
            msgList = msg.split("\n");

            groupMsgMap.put(reqMsg.getGroupId(), msgList);
        }

        Random random = new Random();

        return msgList[random.nextInt(msgList.length)];
    }


    public String getYesterdayDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, -24);
        return dateFormat.format(calendar.getTime());
    }

}
