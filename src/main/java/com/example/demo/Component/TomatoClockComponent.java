package com.example.demo.Component;

import com.example.demo.common.annotation.Instruction;
import com.example.demo.model.msgFromCQ.ReceiveMsg;
import com.example.demo.service.PrivateMsgService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 番茄钟组件
 */
@Component
public class TomatoClockComponent {

    // 用户的番茄钟
    private Map<Long, Tomato> tomatoMap = new HashMap<>();
    // 用户上次中断时间点Map
    volatile private Map<Long, Long> userInterruptePointMap = new HashMap<>();
    /**
     * 番茄的状态：
     * 0-未执行
     * 1-番茄定时中
     * 2-中断状态
     * 3-休息状态
     */
    volatile private Map<Long, Integer> userStatusMap = new HashMap<>();

    @Autowired
    private ThreadPoolExecutor tomatoClockPool;

    @Autowired
    private PrivateMsgService privateMsgService;

    public void setTomatoCoreSize(int coreSize){
        this.tomatoClockPool.setCorePoolSize(coreSize);
    }

    public void setTomatoMaxSize(int maxSize){
        this.tomatoClockPool.setMaximumPoolSize(maxSize);
    }

    /**
     * 番茄类
     */
    private class Tomato implements Runnable {
        private Long userId;
        private Long tomatoCound = 0L;

        public Tomato(Long userId) {
            this.userId = userId;
        }

        public Long getTomatoCound() {
            return tomatoCound;
        }

        @SneakyThrows
        @Override
        public void run() {
            privateMsgService.sendPrivateMsg(this.userId, "新的番茄时间开始啦！开始为你计时25分钟");
            userStatusMap.put(userId, 1);
            synchronized (this) {
                this.wait(1000 * 60 * 25);
            }

            synchronized (this) {
                while (userStatusMap.get(userId) == 2) {
                    Long time = System.currentTimeMillis() - userInterruptePointMap.get(userId);    // 中断时间
                    if (time > 0) {
                        this.wait(time);
                        userInterruptePointMap.put(userId, System.currentTimeMillis());
                    }
                }
            }
            privateMsgService.sendPrivateMsg(this.userId, "番茄时间结束啦！现在开始休息5分钟");
            userStatusMap.put(userId, 3);
            synchronized (this) {
                this.wait(1000 * 60 * 5);
            }
            privateMsgService.sendPrivateMsg(this.userId, "获得一个番茄");
            this.tomatoCound += 1;
            userStatusMap.put(userId, 0);
        }
    }

    /**
     * 为当前用户新增一个番茄钟
     * @param receiveMsg
     * @param params
     * @return
     */
    @Instruction(name = "tomatoClock", descrption = "番茄钟")
    public String addTomatoClock(ReceiveMsg receiveMsg, Map<String, String> params) {
        Long userId = receiveMsg.getUser_id();
        Tomato userThread = tomatoMap.get(userId);
        if (userThread == null) {  // 还没有番茄钟时，创建一个
            Tomato tomato = new Tomato(userId);
            tomatoMap.put(userId, tomato);
            tomatoClockPool.execute(tomato);
        } else if (userStatusMap.get(userId) == 0) {  // 番茄钟还没有启动，将他启动
            tomatoClockPool.execute(userThread);
        } else {    // 番茄钟已经再运行啦
            return "你已经设置了番茄闹钟了哦";
        }
        return "番茄闹钟设置成功";
    }

    /**
     * 为当前用户暂停番茄钟
     * @param receiveMsg
     * @param params
     * @return
     */
    @Instruction(name = "stopTomato", descrption = "暂停番茄钟")
    public String unplanedEvent(ReceiveMsg receiveMsg, Map<String, String> params) {
        Long userId = receiveMsg.getUser_id();
        userInterruptePointMap.put(userId, System.currentTimeMillis());
        userStatusMap.put(userId, 2);
        return "番茄闹钟暂时停下来了";
    }

    /**
     * 为当前用户继续番茄钟
     * @param receiveMsg
     * @param params
     * @return
     */
    @Instruction(name = "continueTomato", descrption = "继续番茄钟")
    public String continueEvent(ReceiveMsg receiveMsg, Map<String, String> params) {
        userStatusMap.put(receiveMsg.getUser_id(), 1);    // 定时状态中
        return "番茄闹钟继续计时";
    }

    /**
     * 获取当前用户的番茄数
     * @param receiveMsg
     * @param params
     * @return
     */
    @Instruction(name = "tomatoCount", descrption = "番茄数量")
    public String getTomatoCount(ReceiveMsg receiveMsg, Map<String, String> params) {
        Tomato tomato = tomatoMap.get(receiveMsg.getUser_id());
        return "你现在有" + tomato.getTomatoCound() + "个番茄";
    }

}
