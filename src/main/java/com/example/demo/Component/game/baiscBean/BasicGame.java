package com.example.demo.Component.game.baiscBean;

import java.util.List;

public class BasicGame
        <GameInfo extends BasicGameInfo, GameEvent extends BasicGameEvent, GameBehavior extends BasicGameBehavior> {

    protected GameInfo gameInfo;

    protected List<GameEvent> gameEventList;

    protected List<GameBehavior> gameBehaviorList;

}
