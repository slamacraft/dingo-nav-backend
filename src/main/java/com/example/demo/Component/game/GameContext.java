package com.example.demo.Component.game;

import com.example.demo.Component.game.baiscBean.BasicGame;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

import java.util.LinkedList;
import java.util.List;

@Component
public class GameContext {

    private List<BasicGame> gameList = new LinkedList<>();

//    private void test(){
//        ClassUtils.getUserClass()
//    }

}
