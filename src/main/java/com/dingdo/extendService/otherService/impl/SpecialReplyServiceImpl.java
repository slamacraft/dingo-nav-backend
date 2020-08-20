package com.dingdo.extendService.otherService.impl;

import com.dingdo.extendService.otherService.ServiceFromApi;
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


    private Map<String, SpecialReplyServiceImpl.RereadMsgInfo> reReadGroupMsgMap = new HashMap<>();

    private int MIN_REREAD_COUNT = 0;

    @Override
    public void rereadGroupMsg(ReqMsg reqMsg) {
        SpecialReplyServiceImpl.RereadMsgInfo rereadMsgInfo = reReadGroupMsgMap.get(reqMsg.getGroupId() + reqMsg.getSelfId());

        if (rereadMsgInfo == null) {
            reReadGroupMsgMap.put(reqMsg.getGroupId() + reqMsg.getSelfId(), new SpecialReplyServiceImpl.RereadMsgInfo(reqMsg));
            return;
        }

        int status = rereadMsgInfo.getStatus(); // 匹配的状态
        List<String> toRereadMessage = rereadMsgInfo.getToRereadMessage();  // 复读的消息池
        List<String> currentMessage = rereadMsgInfo.getCurrentMessage();

        // 如果是同一个人继续发送的消息，放入准备复读的消息池
        if (rereadMsgInfo.getUserId().equals(reqMsg.getUserId())) {
            currentMessage.add(reqMsg.getMessage());
        }
        // 如果不是同一个人发的
        else {
            rereadMsgInfo.setUserId(reqMsg.getUserId());

            if (status == toRereadMessage.size()) {   // 如果确认本次用户产生了复读
                rereadMsgInfo.setStatus(0);
            } else {                                 // 没有复读，覆盖准备复读的语句,并初始化状态
                toRereadMessage.clear();
                toRereadMessage.addAll(currentMessage);
                rereadMsgInfo.setCount(0);
                rereadMsgInfo.setFlag(false);
            }
            rereadMsgInfo.setStatus(0);
            currentMessage.clear();
            currentMessage.add(reqMsg.getMessage());
        }

        toReread(reqMsg, rereadMsgInfo);
    }

    public void toReread(ReqMsg reqMsg, SpecialReplyServiceImpl.RereadMsgInfo rereadMsgInfo) {
        int status = rereadMsgInfo.getStatus(); // 匹配的状态
        List<String> toRereadMessage = rereadMsgInfo.getToRereadMessage();  // 复读的消息池
        List<String> currentMessage = rereadMsgInfo.getCurrentMessage();
        int count = rereadMsgInfo.getCount();   // 复读的次数

        // 已经匹配完成，不再进行匹配
        if (status == toRereadMessage.size()) {
            return;
        }

        if (status == 0) {
            int index = toRereadMessage.indexOf(currentMessage.get(currentMessage.size() - 1));
            if (index != -1) {
                status = index + 1;
                rereadMsgInfo.setStateIndex(index);
            }
            rereadMsgInfo.setStatus(status);
        }else if (toRereadMessage.get(status).equals(currentMessage.get(currentMessage.size() - 1))) {
            status += 1;
        }else {
            return;
        }

        if (status == toRereadMessage.size())  // 消息全部匹配完成
        {
            // 达到最小参与复读的次数, 且没有复读过
            if (count >= MIN_REREAD_COUNT && !rereadMsgInfo.isFlag()) {
                for (int i = rereadMsgInfo.getStateIndex(); i < toRereadMessage.size(); i++) {
                    groupMsgService.sendGroupMsg(reqMsg.getSelfId(), reqMsg.getGroupId(), toRereadMessage.get(i));
                }
                rereadMsgInfo.setFlag(true);
                return;
            }
            rereadMsgInfo.setCount(count + 1);
        }
        rereadMsgInfo.setStatus(status);
    }

    @Override
    public String getRandomGroupMsgYesterday(ReqMsg reqMsg) {

        String groupId = reqMsg.getGroupId();
        String[] msgList = groupMsgMap.get(groupId);
        if (msgList == null || msgList.length < 3) {
            String filePath = reqMsg.getGroupId() + " " + getYesterdayDate() + ".txt";
            String msg = FileUtil.loadFileFromPath(filePath);
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

    /**
     * 消息复读类
     */
    @Data
    private class RereadMsgInfo {
        private int count = 0; // 复读次数
        private int stateIndex = 0; // 复读起始行
        private String userId;
        private List<String> toRereadMessage = new ArrayList<>();
        private List<String> currentMessage = new ArrayList<>();
        private int status = 0;
        private boolean flag = false; // 是否参与复读

        public RereadMsgInfo(ReqMsg reqMsg) {
            this.userId = reqMsg.getUserId();
            this.toRereadMessage.add(reqMsg.getMessage());
            this.currentMessage.add(reqMsg.getMessage());
        }
    }


}
