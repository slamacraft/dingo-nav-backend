package com.dingdo.module.game.cdda.data.component

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "game.cdda")
@EnableConfigurationProperties(CddaInitializer::class)
object CddaInitializer {

    lateinit var dataPath:String

    fun initGame(){
        DataFileLoader.loadData(dataPath)
    }

}
