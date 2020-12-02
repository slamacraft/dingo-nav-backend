package com.dingdo.common.exception

/**
 * 机器人运行时的异常
 * @date 2020/12/2 9:03
 * @author slamacraft 
 * @since JDK 1.8
 * @version 1.0
 */
case class BusinessException(msg:String) extends RuntimeException(msg)

case class CheckException(msg:String) extends RuntimeException(msg)

case class InstructionExecuteException(msg:String) extends RuntimeException(msg)

//case class CQCodeConstructException(msg:String) extends Exception(msg)

case class ClassifierInitializeException(msg:String) extends RuntimeException(msg)
