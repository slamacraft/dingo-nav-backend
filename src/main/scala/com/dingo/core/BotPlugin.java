package com.dingo.core;


public interface BotPlugin {
    String name();

    BotMsg apply(OneMsg msg);
}
