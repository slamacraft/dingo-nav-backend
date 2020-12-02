package com.dingdo.common.exception

import org.springframework.web.bind.annotation.{ExceptionHandler, RestControllerAdvice}

/**
 * @date 2020/12/2 10:50
 * @author slamacraft 
 * @since JDK 1.8
 * @version 1.0
 */
@RestControllerAdvice
class CheckExceptionHandler {

  @ExceptionHandler(Array(classOf[CheckException]))
  def handleBusinessException(e: CheckException): String = e.getMessage

}
