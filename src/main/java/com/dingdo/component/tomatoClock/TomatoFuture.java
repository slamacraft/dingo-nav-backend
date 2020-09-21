package com.dingdo.Component.stopwatch;

import com.dingdo.dao.UserTomatoDao;
import com.dingdo.entities.UserTomatoEntity;
import com.dingdo.msgHandler.service.PrivateMsgService;

/**
 * 一些声明信息
 *
 * @author slamacraft
 * @Description:
 * @date: 2020/9/17 10:55
 * @since JDK 1.8
 */
public class TomatoFuture extends StopWatchFuture {

    private String robotId;

    private String userId;

    private final PrivateMsgService privateMsgService;

    private final UserTomatoDao userTomatoDao;

    public TomatoFuture(String id,
                        Long count,
                        String robotId,
                        String userId,
                        PrivateMsgService privateMsgService,
                        UserTomatoDao userTomatoDao) {
        super();
        super.id = id;
        super.count = count;
        StopWatchTask[] tasks = new StopWatchTask[]{
                new StopWatchTask(this::start, 0),
                new StopWatchTask(this::calculate, 25 * 60),
                new StopWatchTask(this::end, 5 * 60)
        };
        super.taskList = tasks;
        this.robotId = robotId;
        this.userId = userId;
        this.privateMsgService = privateMsgService;
        this.userTomatoDao = userTomatoDao;
    }

    public int getTomato() {
        return userTomatoDao.selectById(userId).getTomato();
    }

    private void start() {
        privateMsgService.sendPrivateMsg(this.robotId, this.userId, "新的番茄时间开始啦！开始为你计时25分钟");
    }

    private void calculate() {
        privateMsgService.sendPrivateMsg(this.robotId, this.userId, "番茄时间结束啦！现在开始休息5分钟");
    }

    private void end() {
        UserTomatoEntity userTomatoEntity = userTomatoDao.selectById(userId);
        userTomatoEntity.setTomato(userTomatoEntity.getTomato() + 1);
        privateMsgService.sendPrivateMsg(this.robotId, this.userId, "获得一个番茄");
    }
}
