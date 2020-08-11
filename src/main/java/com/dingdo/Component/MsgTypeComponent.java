package com.dingdo.Component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 消息类型组件
 */

/**
 * 消息状态组件
 * 记录用户是否处于请求额外功能的状态，并根据固定语句转换该状态
 */
@Component
public class MsgTypeComponent {

    @Autowired
    private StringRedisTemplate redisTemplate;

    // 进入和退出功能的指令集
    private List<String> intoSerInstrcutList = new LinkedList<>();
    private List<String> outSerInstrcutList = new LinkedList<>();

    /**
     * 获取用户是否处于功能请求状态
     *
     * @param userId
     * @return
     */
    public boolean getUserMsgStatus(String userId) {
        String userStatus = redisTemplate.opsForValue().get(userId);
        setUserStatus(userId, "0", 5, TimeUnit.MINUTES);
        return "1".equals(userStatus) ? true : false;
    }

    /**
     * 对应特定的指令，确定是否进入/退出功能模块
     *
     * @param userId
     * @param msg
     * @return 0（没有状态改变），1（启动功能模式），-1（退出功能模式）
     */
    public int msgTriger(String userId, String msg) {
        boolean userMsgStatus = this.getUserMsgStatus(userId);

        if (!userMsgStatus && isIntoServiceInstruction(msg)) {
            setUserStatus(userId, "1", 5, TimeUnit.MINUTES);
            return 1;
        }

        if (userMsgStatus && isOutServiceInstruction(msg)) {
            setUserStatus(userId, "0", 5, TimeUnit.MINUTES);
            return -1;
        }

        return 0;
    }

    /**
     * 设置用户状态
     *
     * @param userId   用户id
     * @param status   状态值
     * @param time     超时时间
     * @param timeUnit 时间单位
     */
    public void setUserStatus(String userId, String status, long time, TimeUnit timeUnit) {
        if(status == null){
            status = "0";
        }
        redisTemplate.opsForValue().set(userId, status, time, timeUnit);
    }

    public boolean isIntoServiceInstruction(String msg) {
        for (String intoMsg : intoSerInstrcutList) {
            if (intoMsg.equals(msg)) {
                return true;
            }
        }
        return false;
    }

    public boolean isOutServiceInstruction(String msg) {
        for (String intoMsg : outSerInstrcutList) {
            if (intoMsg.equals(msg)) {
                return true;
            }
        }
        return false;
    }

    @PostConstruct
    private void initSerList() {
        intoSerInstrcutList.add("进入功能");
        intoSerInstrcutList.add("打开功能");

        outSerInstrcutList.add("退出功能");
        outSerInstrcutList.add("关闭功能");
    }
}
