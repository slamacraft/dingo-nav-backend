package com.dingdo.Component.game.baiscBean;

import java.util.HashMap;
import java.util.Map;

/**
 * 游戏信息基础类
 */
public class BasicGameInfo {

    // 游戏群id
    protected Long groupId;
    // 游戏名称
    protected String gameName;
    // 游戏时间
    protected Long gameTime;
    // 游戏状态
    protected int gameStatus;
    // 游戏玩家信息
    protected Map<Long, BasicGamer> gamerDataMap = new HashMap<>();

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public Long getGameTime() {
        return gameTime;
    }

    public void setGameTime(Long gameTime) {
        this.gameTime = gameTime;
    }

    public int getGameStatus() {
        return gameStatus;
    }

    public void setGameStatus(int gameStatus) {
        this.gameStatus = gameStatus;
    }

    public Map<Long, BasicGamer> getGamerDataMap() {
        return gamerDataMap;
    }
}
