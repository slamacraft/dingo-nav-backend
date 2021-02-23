package com.dingdo.util

import cn.hutool.core.text.CharSequenceUtil
import org.springframework.context.ApplicationContext

object SpringContextUtil {

  private var applicationContext: ApplicationContext = _

  def setApplicationContext(applicationContext: ApplicationContext): Unit = {
    SpringContextUtil.applicationContext = applicationContext
  }

  /**
   * 获取applicationContext
   *
   * @return 返回[[ApplicationContext]]
   */
  def getApplicationContext: ApplicationContext = applicationContext

  /**
   * 通过name获取 Bean.
   *
   * @param name bean的名称
   * @return 返回[[Any]]类型的bean
   */
  def getBean(name: String): Any = getBean(getApplicationContext, name, null)

  /**
   * 通过class获取Bean.
   *
   * @param clazz bean的类型
   * @tparam T bean的类型泛型
   * @return [[T]]
   */
  def getBean[T](clazz: Class[T]): T = getBean(getApplicationContext, null, clazz)

  /**
   * 通过name,以及Clazz返回指定的Bean
   *
   * @param name  bean的名称
   * @param clazz bean的类型
   * @tparam T bean的类型泛型
   * @return [[T]]
   */
  def getBean[T](name: String, clazz: Class[T]): T = getBean(getApplicationContext, name, clazz)


  private def getBean[T](context: ApplicationContext, name: String, clazz: Class[T]): T = {
    if (context == null) throw new NullPointerException("applicationContext为空")
    if (CharSequenceUtil.isBlank(name)) context.getBean(clazz)
    else if (clazz == null) context.getBean(name).asInstanceOf[T]
    else context.getBean(name, clazz)
  }
}
