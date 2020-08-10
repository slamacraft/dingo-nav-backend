package com.dingdo.Component.gameComponent.gameBean;

import com.dingdo.Component.componentBean.MsgBean;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class BasicGame implements Runnable{

    // 本局游戏名称
    protected String gameName;

    // 本局游戏的群组id
    protected long groupId;

    /**
     * 游戏状态；
     * 0=未开始
     * 1=已经开始
     */
    protected int gameStatus = 0;

    // 游戏时间
    protected long gameTime;

    // 消息队列
    protected Queue<MsgBean> msgQueue = new LinkedBlockingQueue<>();

    @Override
    public void run() {
        mainGame();
    }

    public abstract String startGame();

    public abstract String stopGame();

    public abstract String gameOver();

    public abstract String triggerBehavior();

    public abstract String gameEvent();

    public abstract String mainGame();

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public long getGroupId() {
        return groupId;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }

    public int getGameStatus() {
        return gameStatus;
    }

    public void setGameStatus(int gameStatus) {
        this.gameStatus = gameStatus;
    }

    public long getGameTime() {
        return gameTime;
    }

    public void setGameTime(long gameTime) {
        this.gameTime = gameTime;
    }

    public Queue<MsgBean> getMsgQueue() {
        return msgQueue;
    }

    public void setMsgQueue(Queue<MsgBean> msgQueue) {
        this.msgQueue = msgQueue;
    }
}
