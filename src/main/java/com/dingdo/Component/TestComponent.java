//package com.dingdo.Component;
//
//import com.dingdo.model.msgFromMirai.ReqMsg;
//import com.dingdo.service.impl.GroupMsgServiceImpl;
//import lombok.Data;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import java.util.*;
//
///**
// * 一些声明信息
// *
// * @author slamacraft
// * @Description:
// * @date: 2020/8/20 17:19
// * @since JDK 1.8
// */
//public class TestComponent {
//
//    @Autowired
//    private GroupMsgServiceImpl groupMsgService;
//
//    private Map<String, RereadMsgQueue> reReadGroupMsgMap = new HashMap<>();
//
//    private int MIN_REREAD_COUNT = 2;
//
//    @Data
//    class RereadMsgInfo {
//        private String userId;
//        private List<String> message = new ArrayList<>();
//        private int status = 0;
//
//        public RereadMsgInfo(ReqMsg reqMsg) {
//            this.userId = reqMsg.getUserId();
//            this.message.add(reqMsg.getMessage());
//        }
//    }
//
//    @Data
//    class RereadMsgQueue {
//        volatile private List<RereadMsgInfo> msgInfoList = new LinkedList<>();
//        private RereadMsgInfo currentMsgInfo;
//        private boolean flag = false; // 是否参与复读
//
//        public RereadMsgQueue(ReqMsg reqMsg) {
//            RereadMsgInfo rereadMsgInfo = new RereadMsgInfo(reqMsg);
//            currentMsgInfo = rereadMsgInfo;
//            msgInfoList.add(rereadMsgInfo);
//        }
//    }
//
//
//    public void rereadGroupMsg(ReqMsg reqMsg) {
//        RereadMsgQueue rereadMsgQueue = reReadGroupMsgMap.get(reqMsg.getGroupId() + reqMsg.getSelfId());
//
//        if (rereadMsgQueue == null) {
//            reReadGroupMsgMap.put(reqMsg.getGroupId() + reqMsg.getSelfId(), new RereadMsgQueue(reqMsg));
//            return;
//        }
//
//        List<RereadMsgInfo> msgInfoList = rereadMsgQueue.getMsgInfoList();
//        RereadMsgInfo currentMsgInfo = rereadMsgQueue.getCurrentMsgInfo();
//
//
//        // 如果是同一个人继续发送的消息
//        if (currentMsgInfo.getUserId().equals(reqMsg.getUserId())) {
//            currentMsgInfo.getMessage().add(reqMsg.getMessage());
//        } else {
//            msgInfoList.get(0).setStatus(0);
//            msgInfoList.add(currentMsgInfo);
//            rereadMsgQueue.setCurrentMsgInfo(new RereadMsgInfo(reqMsg));
//        }
//
//        toReread(reqMsg, rereadMsgQueue);
//    }
//
//    public void toReread(ReqMsg reqMsg, RereadMsgQueue rereadMsgQueue) {
//        List<RereadMsgInfo> msgInfoList = rereadMsgQueue.getMsgInfoList();
//
//        // 最少也要2句话才算复读吧
//        if (msgInfoList.size() < 1) {
//            return;
//        }
//
//
//        int status = rereadMsgQueue.getCurrentMsgInfo().getStatus(); // 匹配的状态
//        int count = msgInfoList.size() + 1;   // 复读的次数
//
//        List<String> currentMsg = rereadMsgQueue.getCurrentMsgInfo().getMessage();  // 复读的消息池
//
//        RereadMsgInfo startMsg = msgInfoList.get(0);
//        RereadMsgInfo lastMsg = msgInfoList.get(count - 1);
//
//        if (startMsg.getStatus() == startMsg.getMessage().size()) {
//            return;
//        }
//
//        // 新发送的消息如果从没有匹配过任何消息，优先与队列尾进行复读匹配
//        if (startMsg.getStatus() == 0) {
//            // 查找第一句话在最新的发送者的消息中的位置
//            int index = this.getIndexOf(lastMsg.getMessage(), currentMsg.get(currentMsg.size() - 1));
//            if (index == 0) {   // 和第一句话匹配上，那么接下来的匹配肯定和最初发送者的相同
//                status = index + 1;
//                startMsg.setStatus(status);
//            } else if (index > 0) {   // 和非第一句话匹配上，那么和接下来的匹配和最初发送者的不同了
//                // 将最新发送者作为最初发送者
//                msgInfoList.clear();
//                msgInfoList.add(lastMsg);
//                // 将匹配的第一句话位置以前的消息列表删除
//                List<String> message = lastMsg.getMessage();
//                lastMsg.setMessage(message.subList(index, message.size()));
//
//                status = index + 1;
//                startMsg = lastMsg;
//                startMsg.setStatus(status);
//            } else { // 如果第一句话未能与上一个发送者匹配上
//                msgInfoList.clear();    // 由于已经确认不会产生复读，清空消息队列
//                rereadMsgQueue.setFlag(false);
//                msgInfoList.add(rereadMsgQueue.getCurrentMsgInfo());    // 将该用户的消息作为初始复读
//                return;
//            }
//        }
//        // 如果
//        else if (startMsg.getMessage().get(status - 1).equals(currentMsg.get(currentMsg.size() - 1))) {
//            status += 1;
//            startMsg.setStatus(status);
//        }
//
//        if (status == startMsg.getMessage().size())  // 消息全部匹配完成
//        {
//            // 达到最小参与复读的次数, 且没有复读过
//            if (count >= MIN_REREAD_COUNT && !rereadMsgQueue.isFlag()) {
//                for (String msg : startMsg.getMessage()) {
//                    groupMsgService.sendGroupMsg(reqMsg.getSelfId(), reqMsg.getGroupId(), msg);
//                }
//                rereadMsgQueue.setFlag(true);
//            }
//        }
//    }
//
//    private boolean equals(String msg1, String msg2) {
//        if (msg1 == msg2) {
//            return true;
//        }
//        if (msg1 == null || msg2 == null) {
//            return false;
//        }
//        String textMsg1 = msg1.replaceAll("\\[CQ:image.*?\\]", "");
//        String imageMsg1 = "";
//        String textMsg2 = msg2.replaceAll("\\[CQ:image.*?\\]", "");
//        String imageMsg2 = "";
//        if (msg1.contains("CQ:image")) {
//            imageMsg1 = msg1.split("\\[CQ:image,image=")[1].split(",")[0];
//        }
//        if (msg2.contains("CQ:image")) {
//            imageMsg2 = msg2.split("\\[CQ:image,image=")[1].split(",")[0];
//        }
//
//        return textMsg1.equals(textMsg2) && imageMsg1.equals(imageMsg2);
//    }
//
//    private int getIndexOf(List<String> msgList, String msg) {
//        for (int i = 0; i < msgList.size(); i++) {
//            if (this.equals(msgList.get(i), msg)) {
//                return i;
//            }
//        }
//        return -1;
//    }
//
//
//}
