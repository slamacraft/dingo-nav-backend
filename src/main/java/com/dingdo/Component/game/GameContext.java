package com.dingdo.Component.game;

import com.dingdo.Component.game.baiscBean.BasicGame;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

@Component
public class GameContext {

    private List<BasicGame> gameList = new LinkedList<>();

//    private void test(){
//        ClassUtils.getUserClass()
//    }

}
