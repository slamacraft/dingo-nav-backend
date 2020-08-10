package com.dingdo.Component.gameComponent;

import com.dingdo.Component.gameComponent.gameBean.BasicGame;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

@Component
public class GameComponent {

    @Autowired
    private ThreadPoolExecutor gameRegisterPool;

    private List<BasicGame> gameList = new LinkedList<>();

    public void registerGame(BasicGame game){
        gameRegisterPool.execute(game);
    }

    public BasicGame getGameData(Long groupId, String gameName){
        for(BasicGame game : gameList){
            if(game.getGroupId() == groupId && game.getGameName().equals(gameName)){
                return game;
            }
        }
        return null;
    }

    public List<BasicGame> getGameList() {
        return gameList;
    }
}
