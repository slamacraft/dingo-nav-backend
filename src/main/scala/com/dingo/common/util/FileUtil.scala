package com.dingo.common.util

import lombok.extern.slf4j.Slf4j
import org.springframework.boot.system.ApplicationHome

import java.io._
import java.util.stream.Collectors
import scala.language.implicitConversions


/**
 * 文件操作工具类
 * 可以加载文件、将字符添加到文件末尾
 */
@Slf4j
object FileUtil { // 使用log4j打印日志

  /**
   * 获取jar路径的静态内部类
   */
  private object JarPathHolder {
    private[FileUtil] val jarUrl: String = {
      val home = new ApplicationHome(FileUtil.getClass)
      val jarFile: File = home.getSource
      //      if (jarFile == null || jarFile.getParentFile.toString ==null || ) {
      //        "/python/CQPython/static"
      //      } else {
      //        jarFile.getParentFile.toString
      //      }
      ""
    }
  }

  implicit def fileToString(br: BufferedReader): String = {
    br.lines().collect(Collectors.joining())
  }

  /**
   * 将文件读取为String
   *
   * @param path
   * @return
   */
  def loadFileFromResource[T](path: String)(using op: BufferedReader => T): T = {
    load(FileUtil.getClass.getClassLoader.getResourceAsStream(path))
  }

  def loadFile[T](path: String)(implicit op: BufferedReader => T): T = {
    load {
      Option(new File(path))
        .filter(it => it.isFile && it.exists)
        .map(it => new FileInputStream(it))
        .getOrElse(throw new RuntimeException("找不到指定的文件，请确认文件路径是否正确!"))
    }
  }

  @throws[IOException]
  def load[T](getInputStream: => InputStream)(using op: BufferedReader => T): T = {
    val reader = new InputStreamReader(getInputStream, "UTF-8")
    val br: BufferedReader = new BufferedReader(reader)
    try {
      op(br)
    } finally {
      getInputStream.close()
      reader.close()
      br.close()
    }
  }

  def loadFileFromJarPath(path: String): String = {
    loadFile(JarPathHolder.jarUrl + "/message/" + path)
  }

  def saveMsgToFile(path: String, text: String): Unit = { // 获取当前jar包所在的文件路径
    val filePath = JarPathHolder.jarUrl + "/message/" + path
    write(new FileOutputStream(filePath, true)) { writer =>
      writer.write(text)
    }
  }

  def write(output: => OutputStream)(op: BufferedWriter => Unit): Unit = {
    val writer = new OutputStreamWriter(output, "UTF-8")
    val bw = new BufferedWriter(writer)
    try {
      op(bw)
    } finally {
      bw.close()
      writer.close()
    }
  }


  /**
   * 清空文件
   *
   * @param filePath
   */
  def clearFile(filePath: String): Unit = clearFile(new File(filePath))

  def clearFile(file: File): Unit = {
    if (!file.exists) file.createNewFile
    val fileWriter: FileWriter = new FileWriter(file)
    try {
      fileWriter.write("")
      fileWriter.flush()
    } finally {
      fileWriter.close()
    }
  }

  def getImagePath(imageName: String): String = JarPathHolder.jarUrl + "/image/" + imageName + ".jpg"
}
