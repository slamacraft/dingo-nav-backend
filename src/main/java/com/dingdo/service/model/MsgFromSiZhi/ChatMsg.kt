package com.dingdo.extendService.model.MsgFromSiZhi

/**
 * @date 2020/9/23 16:08
 * @author slamacraft
 * @since JDK 1.8
 * @version 1.0
 */

data class ChatMsg(var message: String, var data: Data)

data class Data(var type: Int, var info: Info)

data class Info(var text: String)