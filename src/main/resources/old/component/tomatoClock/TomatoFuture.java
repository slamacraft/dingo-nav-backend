package com.dingdo.component.tomatoClock;

import com.dingdo.component.stopwatch.StopWatchFuture;
import com.dingdo.component.stopwatch.StopWatchTask;
import com.dingdo.model.entities.UserTomatoEntity;
import com.dingdo.msgHandler.service.PrivateMsgService;
import com.dingdo.model.service.UserTomatoService;

/**
 * 番茄钟任务类
 *
 * @author slamacraft
 * @date: 2020/9/17 10:55
 * @since JDK 1.8
 * @see StopWatchFuture
 */
public class TomatoFuture extends StopWatchFuture {

    // 机器人id
    private String robotId;
    // 用户id
    private String userId;

    private final PrivateMsgService privateMsgService;
    private final UserTomatoService userTomatoService;

    public TomatoFuture(String id,
                        Long count,
                        String robotId,
                        String userId,
                        PrivateMsgService privateMsgService,
                        UserTomatoService userTomatoMapper) {
        super();
        super.id = id;
        super.count = count;
        super.taskList = new StopWatchTask[]{
                new StopWatchTask(this::start, 0),
                new StopWatchTask(this::calculate, 25 * 60),
                new StopWatchTask(this::end, 5 * 60)
        };
        this.robotId = robotId;
        this.userId = userId;
        this.privateMsgService = privateMsgService;
        this.userTomatoService = userTomatoMapper;
    }

    public int getTomato() {
        return userTomatoService.getById(userId).getTomato();
    }

    private void start() {
        privateMsgService.sendPrivateMsg(this.robotId, this.userId, "新的番茄时间开始啦！开始为你计时25分钟");
    }

    private void calculate() {
        privateMsgService.sendPrivateMsg(this.robotId, this.userId, "番茄时间结束啦！现在开始休息5分钟");
    }

    private void end() {
        UserTomatoEntity userTomatoEntity = userTomatoService.getById(userId);
        userTomatoEntity.setTomato(userTomatoEntity.getTomato() + 1);
        privateMsgService.sendPrivateMsg(this.robotId, this.userId, "获得一个番茄");
    }
}
