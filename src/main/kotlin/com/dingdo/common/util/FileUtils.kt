package com.dingdo.common.util

import cn.hutool.core.util.StrUtil
import org.springframework.boot.system.ApplicationHome
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

/**
 * @date 2020/9/24 11:20
 * @author slamacraft
 * @since JDK 1.8
 * @version 1.0
 */
object FileUtils {


    object JarPath {
        val jarPath = ApplicationHome(FileUtils::class.java).source.parentFile.toString()
    }

    /**
     * 以utf-8的格式打开resource下的[path]文件
     * @return [path]文件的文本
     */
    @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    @JvmStatic
    fun loadFileFromResource(path: String): String {
        return FileUtils::class.java.getResourceAsStream(path.requireNonBlank())
                .bufferedReader()
                .readText()
    }


    /**
     * 相对于jar的路径[relativePath]打开文件
     */
    @JvmStatic
    fun loadFileRelativeToJar(relativePath: String): String {
        return loadFile(JarPath.jarPath + relativePath)
    }

    /**
     * 以utf-8的格式打开[path]下的文件
     * @return [path]文件的文本
     */
    @JvmStatic
    fun loadFile(path: String): String {
        return File(path.requireNonBlank()).existOrCreate().readText()
    }


    /**
     * 获取jar包路径地址
     */
    @JvmStatic
    fun getJarPath(): String {
        return JarPath.jarPath
    }


    /**
     * 将[text]文本添加到[path]文件末尾
     */
    @JvmStatic
    fun appendTextRelativeToJar(path: String, text: String) {
        appendText(JarPath.jarPath + path, text)
    }

    /**
     * 将[text]文本添加到[path]文件末尾
     */
    @JvmStatic
    fun appendText(path: String, text: String) {
        File(path.requireNonBlank()).existOrCreate().appendText(text)
    }

    /**
     * 清空[path]文件内容
     */
    @JvmStatic
    fun clearFile(path: String) {
        File(path.requireNonBlank()).existOrCreate().writeText("")
    }


    /**
     * 相对于jar路径保存图片[image]
     */
    @JvmStatic
    fun saveImageRelativeToJar(image: BufferedImage, relativePath: String): String {
        return saveImage(image, JarPath.jarPath + relativePath)
    }


    /**
     * 保存图片[image]到路径[path]
     */
    @JvmStatic
    fun saveImage(image: BufferedImage, path: String): String {
        val file = File(path.requireNonBlank()).existOrCreate()
        ImageIO.write(image, "jpg", file)
        return path
    }

}


fun String.requireNonBlank(): String {
    if (StrUtil.isBlank(this)) {
        throw RuntimeException("文件地址不能为空")
    }
    return this
}

fun File.existOrCreate(): File {
    if (!this.exists()) {
        this.createNewFile()
    }
    return this
}
