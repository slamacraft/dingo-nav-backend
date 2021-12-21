package com.dingdo.module.entity

import com.dingdo.config.configuration.mybatis.BaseEntity

class GamerAttr : BaseEntity() {
    lateinit var name:String
    var hp:Int = 12
    var hpMax:Int = 12
    var atk:Float = 3.5f
    var def:Int = 3
    var coin:Int = 3
    var key:Int = 1
    lateinit var props:ByteArray
}
