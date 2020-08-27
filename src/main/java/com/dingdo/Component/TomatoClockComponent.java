package com.dingdo.Component;

import com.dingdo.common.annotation.Instruction;
import com.dingdo.dao.UserTomatoDao;
import com.dingdo.entities.UserTomatoEntity;
import com.dingdo.model.msgFromMirai.ReqMsg;
import com.dingdo.service.PrivateMsgService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 番茄钟组件
 */
@Component
public class TomatoClockComponent {

    // 用户的番茄钟
    private Map<String, Tomato> tomatoMap = new HashMap<>();
    // 用户上次中断时间点Map
    volatile private Map<String, Long> userInterruptePointMap = new HashMap<>();
    /**
     * 番茄的状态：
     * 0-未执行
     * 1-番茄定时中
     * 2-中断状态
     * 3-休息状态
     */
    volatile private Map<String, Integer> userStatusMap = new HashMap<>();

    @Autowired
    private ThreadPoolExecutor tomatoClockPool;

    @Autowired
    private PrivateMsgService privateMsgService;

    @Autowired(required = false)
    private UserTomatoDao userTomatoDao;


    public void setTomatoCorePoolSize(int coreSize) {
        this.tomatoClockPool.setCorePoolSize(coreSize);
    }

    public void setTomatoMaxPoolSize(int maxSize) {
        this.tomatoClockPool.setMaximumPoolSize(maxSize);
    }


    /**
     * 番茄类
     */
    private class Tomato implements Runnable {
        private String robotId;
        private String userId;

        public Tomato(String robotId, String userId) {
            this.robotId = robotId;
            this.userId = userId;
        }


        @Override
        @SneakyThrows
        @Transactional
        public void run() {
            privateMsgService.sendPrivateMsg(this.robotId, this.userId, "新的番茄时间开始啦！开始为你计时25分钟");
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
            privateMsgService.sendPrivateMsg(this.robotId, this.userId, "番茄时间结束啦！现在开始休息5分钟");
            userStatusMap.put(userId, 3);
            synchronized (this) {
                this.wait(1000 * 60 * 5);
            }
            privateMsgService.sendPrivateMsg(this.robotId, this.userId, "获得一个番茄");

            UserTomatoEntity userTomatoEntity = userTomatoDao.selectById(this.userId);
            if (userTomatoEntity == null) {
                UserTomatoEntity toInsertEntity = new UserTomatoEntity();
                toInsertEntity.setTomato(1);
                userTomatoDao.insert(toInsertEntity);
            } else {
                userTomatoEntity.setTomato(userTomatoEntity.getTomato() + 1);
                userTomatoDao.updateById(userTomatoEntity);
            }

            userStatusMap.put(userId, 0);
        }
    }


    /**
     * 为当前用户新增一个番茄钟
     *
     * @param reqMsg
     * @param params
     * @return
     */
    @Instruction(description = "番茄钟")
    public String addTomatoClock(ReqMsg reqMsg, Map<String, String> params) {
        String userId = reqMsg.getUserId();
        Tomato userThread = tomatoMap.get(userId);

        if (userThread == null) {  // 还没有番茄钟时，创建一个
            Tomato tomato = new Tomato(reqMsg.getSelfId(), userId);
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
     *
     * @param reqMsg
     * @param params
     * @return
     */
    @Instruction(description = "暂停番茄钟", inMenu = false)
    public String unplanedEvent(ReqMsg reqMsg, Map<String, String> params) {
        String userId = reqMsg.getUserId();
        userInterruptePointMap.put(userId, System.currentTimeMillis());
        userStatusMap.put(userId, 2);
        return "番茄闹钟暂时停下来了";
    }


    /**
     * 为当前用户继续番茄钟
     *
     * @param reqMsg
     * @param params
     * @return
     */
    @Instruction(description = "继续番茄钟", inMenu = false)
    public String continueEvent(ReqMsg reqMsg, Map<String, String> params) {
        userStatusMap.put(reqMsg.getUserId(), 1);    // 定时状态中
        return "番茄闹钟继续计时";
    }


    /**
     * 获取当前用户的番茄数
     *
     * @param reqMsg
     * @param params
     * @return
     */
    @Instruction(description = "番茄数量", inMenu = false)
    public String getTomatoCount(ReqMsg reqMsg, Map<String, String> params) {
        UserTomatoEntity userTomatoEntity = userTomatoDao.selectById(reqMsg.getUserId());
        if(userTomatoEntity != null){
            return "你现在有" + userTomatoEntity.getTomato() + "个番茄";
        }
        return "你现在还未获得过番茄，快使用番茄钟试试吧！";
    }

}
