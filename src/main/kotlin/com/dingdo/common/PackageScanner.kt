package com.dingdo.common

import java.io.File
import java.net.JarURLConnection

object PackageScanner {

    val classes: HashSet<Class<*>> = HashSet()
    private val classLoader: ClassLoader = Thread.currentThread().contextClassLoader

    fun doScan(baseClazz: Class<*>) {
        doScan(baseClazz.`package`.name)
    }

    fun doScan(basePackage: String) {
        val url = (classLoader.getResource(basePackage.replace(".", "/"))
            ?: throw ClassNotFoundException("$basePackage 不存在"))

        if (url.protocol == "file") {
            getFileClasses(basePackage)
        } else if (url.protocol == "jar") {
            getJarClasses(basePackage)
        }
    }

    private fun getFileClasses(basePackage: String) =
        File(classLoader.getResource(basePackage.replace(".", "/"))!!.toURI())
            .walk()
            .filter { it.name.endsWith(".class") }
            .forEach {
                val path = it.parent.replace("\\", ".")
                val packagePath = path.substring(path.indexOf(basePackage))
                try {
                    classes.add(classLoader.loadClass("${packagePath}.${it.name.replace(".class", "")}"))
                } catch (_: Exception) {
                    println("未加载类 ${packagePath}.${it.name.replace(".class", "")}")
                }
            }

    private fun getJarClasses(basePackage: String) {
        val jarURLConnection = classLoader.getResource(basePackage.replace(".", "/"))!!
            .openConnection() as JarURLConnection
        jarURLConnection.jarFile.entries()
            .toList()
            .filter {
                it.name.replace("/", ".").startsWith("packageName")
                        && it.name.endsWith(".class")
            }.forEach {
                try {
                    classes.add(
                        classLoader.loadClass(
                            "${basePackage}.${
                                it.name.replace(".class", "").replace("/", ".")
                            }"
                        )
                    )
                } catch (_: Exception) {
                }
            }
    }
}
