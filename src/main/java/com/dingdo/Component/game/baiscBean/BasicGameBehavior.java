package com.dingdo.Component.game.baiscBean;

/**
 * 游戏行为基础类
 */
public abstract class BasicGameBehavior<GameInfo extends BasicGameInfo>{

    // 游戏信息
    protected GameInfo gameInfo;
    // 行为的名称
    protected String behaviorName;
    // 行为发起时间
    protected Long behaviorTime;

    public abstract boolean behaviorBegin();

    public abstract void behavior();
}
