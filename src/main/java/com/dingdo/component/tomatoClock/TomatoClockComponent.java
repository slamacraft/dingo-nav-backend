package com.dingdo.Component.stopwatch;

import com.dingdo.common.annotation.Instruction;
import com.dingdo.dao.UserTomatoDao;
import com.dingdo.msgHandler.model.ReqMsg;
import com.dingdo.msgHandler.service.PrivateMsgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 一些声明信息
 *
 * @author slamacraft
 * @Description:
 * @date: 2020/9/17 15:34
 * @since JDK 1.8
 */
@Component
public class TomatoClockComponent {

    private final StopWatchRegister stopWatchRegister;

    private final PrivateMsgService privateMsgService;

    private final UserTomatoDao userTomatoDao;

    private static final String TOMATO_CLOCK_NAME = "tomato";

    @Autowired
    public TomatoClockComponent(StopWatchRegister stopWatchRegister, PrivateMsgService privateMsgService, UserTomatoDao userTomatoDao) {
        this.stopWatchRegister = stopWatchRegister;
        this.privateMsgService = privateMsgService;
        this.userTomatoDao = userTomatoDao;
    }

    private String getTomatoId(ReqMsg reqMsg) {
        return TOMATO_CLOCK_NAME + reqMsg.getUserId();
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
        String id = getTomatoId(reqMsg);
        StopWatchFuture future = stopWatchRegister.getFuture(id);
        if(future != null){
            return "你已经设置了番茄闹钟了哦";
        }
        TomatoFuture tomatoFuture = new TomatoFuture(id, 1L,
                reqMsg.getSelfId(),
                reqMsg.getUserId(),
                privateMsgService,
                userTomatoDao);
        stopWatchRegister.addFuture(tomatoFuture);
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
        String id = getTomatoId(reqMsg);
        boolean flag = stopWatchRegister.stopFuture(id);
        if(flag){
            return "番茄闹钟暂时停下来了";
        }
        return "你还没有计时中的番茄钟，无法暂停";
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
        String id = getTomatoId(reqMsg);
        boolean flag = stopWatchRegister.continueFuture(id);
        if(flag){
            return "番茄闹钟继续计时";
        }
        return "你还没有暂停中的番茄钟";
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
        String id = getTomatoId(reqMsg);
        StopWatchFuture future = stopWatchRegister.getFuture(id);
        if (future != null) {
            return "你现在有" + ((TomatoFuture) future).getTomato() + "个番茄";
        }
        return "你现在还未获得过番茄，快使用番茄钟试试吧！";
    }
}
