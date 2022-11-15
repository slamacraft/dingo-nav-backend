package com.dingdo.common

import java.io.{File, IOException}
import java.net.{JarURLConnection, URL}
import java.util
import java.util.function.Predicate
import java.util.jar.{JarEntry, JarFile}
import scala.collection.mutable

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
    getClasses(packageName)
    for {clazz <- classes if filter.test(clazz)} yield clazz
  }

  @throws[ClassNotFoundException]
  def getClasses(packageName: String): Unit = {
    val url: URL = classLoader.getResource(packageName.replace('.', '/'))
    if (url == null) {
      throw new ClassNotFoundException("该包不存在：" + packageName)
    }
    val protocol = url.getProtocol
    if ("file" == protocol) {
      getLocalClasses(packageName)
    }
    else if ("jar" == protocol) {
      getJarClasses(packageName)
    }
  }

  def getLocalClasses(packageName: String): Unit = {
    val file: File = new File(classLoader.getResource(packageName.replace('.', '/')).toURI)

    def foo(file: File): Boolean = { // 如果是文件夹，递归扫描
      if (file.isDirectory) {
        if (packageName.isEmpty) {
          getLocalClasses(file.getName)
        } else {
          getLocalClasses(packageName + "." + file.getName)
        }
      }
      // 如果是文件，尝试加载.class文件
      if (file.getName.endsWith(".class")) {
        this.classes.add(classLoader.loadClass(packageName + "." + file.getName.replace(".class", "")))
        return true
      }
      false
    }

    // 遍历路径下所有的文件与文件夹
    file.listFiles(foo(_))
  }

  def getJarClasses(packageName: String): Unit = {
    val pathName: String = packageName.replace(".", "/")
    var jarFile: JarFile = null
    try {
      val url: URL = classLoader.getResource(pathName)
      val jarURLConnection: JarURLConnection = url.openConnection.asInstanceOf[JarURLConnection]
      jarFile = jarURLConnection.getJarFile
    } catch {
      case e: IOException =>
        throw new RuntimeException("未找到策略资源")
    }
    val jarEntries: util.Enumeration[JarEntry] = jarFile.entries
    while ( {
      jarEntries.hasMoreElements
    }) {
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
          getJarClasses(prefix)
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