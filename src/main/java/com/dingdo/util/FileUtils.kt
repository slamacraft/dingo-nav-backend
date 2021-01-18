package com.dingdo.util

import cn.hutool.core.util.StrUtil
import com.dingdo.service.enums.ClassicEnum
import org.apache.log4j.Logger
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

    val logger: Logger = Logger.getLogger(FileUtils::class.java)

    object JarPath {
        val jarPath = ApplicationHome(FileUtils::class.java).source.parentFile.toString()
    }

    /**
     * 获取[path]路径下所有的文件，包括子文件
     * @return 返回一个[Map],key=文件路径，value=文件名称
     */
    @JvmStatic
    fun getFiles(path: String): Map<String, String> {
        val result = HashMap<String, String>(ClassicEnum.values().size)
        val file = File(path.requireNonBlank()).existOrCreate()

        file.walk().maxDepth(3)
                .filter { it.isFile }
                .filter { it.extension == "txt" }
                .forEach { result[it.toString()] = it.name }
        return result
    }


    /**
     * 以utf-8的格式打开resource下的[path]文件
     * @return [path]文件的文本
     */
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
     * 通过[imageUrl]将图片保存到到路径[path]
     */
    @JvmStatic
    fun saveImageFromUrl(imageUrl: String, path: String): String {
        return saveImage(ImageUtil.getImageFromURL(imageUrl), path)
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