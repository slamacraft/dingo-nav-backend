package com.dingo.common

import java.io.File
import java.net.{JarURLConnection, URL}
import java.util.function.Predicate
import java.util.jar.{JarEntry, JarFile}
import scala.collection.mutable
import scala.language.implicitConversions

object PackageScanner {
  def apply(): PackageScanner = new PackageScanner(Thread.currentThread.getContextClassLoader)
}

class PackageScanner(var classLoader: ClassLoader) {

  val classes: mutable.Set[Class[_]] = new mutable.HashSet[Class[_]]

  @throws[ClassNotFoundException]
  def findAllClasses(packageName: String): mutable.Set[Class[_]] = {
    findAllClasses(packageName, (_: Class[_]) => true)
  }

  @throws[ClassNotFoundException]
  def findAllClasses(packageName: String, filter: Predicate[Class[_]]): mutable.Set[Class[_]] = {
    scanClasses(packageName)
    for {clazz <- classes if filter.test(clazz)} yield clazz
  }


  def strToFile(packageName: String): File = {
    new File(classLoader.getResource(packageName.replace('.', '/')).toURI)
  }


  implicit class PackageWalker(val packageName: String) {

    def walkClass(filter: Class[_] => Boolean = _ => true): mutable.ArrayBuffer[Class[_]] = {
      val result = mutable.ArrayBuffer[Class[_]]()
      val searchStack = new mutable.Queue[String]()

      searchStack += packageName
      while (searchStack.nonEmpty) {
        val searchPackage = searchStack.dequeue()
        val searchFile = strToFile(searchPackage)

        if (searchFile.isFile && searchPackage.endsWith(".class")) {
          val clazz = classLoader.loadClass(s"$searchPackage.${searchFile.getName.replace(".class", "")}")
          if (filter(clazz)) {
            result += clazz
          }
        } else if(searchFile.isDirectory){
          searchFile.listFiles().foreach{it=>
            searchStack += s"$searchPackage.${it.getName}"
          }
        }
      }

      result
    }
  }

  @throws[ClassNotFoundException]
  def scanClasses(packageName: String): Unit = {
    Option(classLoader.getResource(packageName.replace('.', '/')))
      .orElse(throw new ClassNotFoundException("该包不存在：" + packageName))
      .foreach(url =>
        url.getProtocol match {
          case "file" => scanLocalClasses(packageName)
          case "jar" => scanJarClasses(packageName)
        }
      )
  }

  private def scanLocalClasses(packagePath: String): Unit = {
    classes ++= packagePath.walkClass()
  }

  private def scanJarClasses(packagePath: String): Unit = {
    val pathName: String = packagePath.replace(".", "/")
    val url: URL = classLoader.getResource(pathName)
    val jarURLConnection: JarURLConnection = url.openConnection.asInstanceOf[JarURLConnection]
    val jarFile: JarFile = jarURLConnection.getJarFile

    val jarEntries = jarFile.entries
    while (jarEntries.hasMoreElements) {
      val jarEntry: JarEntry = jarEntries.nextElement
      val jarEntryName: String = jarEntry.getName

      if (jarEntryName.contains(pathName) && !(jarEntryName == pathName + "/")) { //递归遍历子目录
        if (jarEntry.isDirectory) {
          val clazzName: String = jarEntry.getName.replace("/", ".")
          val endIndex: Int = clazzName.lastIndexOf(".")
          var prefix: String = null
          if (endIndex > 0) {
            prefix = clazzName.substring(0, endIndex)
          }
          scanJarClasses(prefix)
        }
        if (jarEntry.getName.endsWith(".class")) {
          var clazz: Class[_] = null
          try clazz = classLoader.loadClass(jarEntry.getName.replace("/", ".").replace(".class", ""))
          catch {
            case e: ClassNotFoundException =>
              e.printStackTrace()
          }
          //判断，如果符合，添加
          if (clazz != null) {
            this.classes.add(clazz)
          }
        }
      }
    }
  }
}