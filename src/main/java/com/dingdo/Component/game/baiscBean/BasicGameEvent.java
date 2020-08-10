package com.dingdo.Component.game.baiscBean;

/**
 * 游戏事件基础类
 */
public abstract class BasicGameEvent <GameInfo extends BasicGameInfo>{

    // 游戏的信息
    protected GameInfo gameInfo;

    // 事件名称
    protected String eventName;

    // 事件优先级
    protected int priority;

    /**
     * 事件的开始条件
     * @return
     */
    public abstract boolean eventBegin();

    /**
     * 事件的执行主体
     */
    public abstract void event();

    /**
     * 事件的结束条件
     * @return
     */
    public abstract boolean eventEnd();

}
