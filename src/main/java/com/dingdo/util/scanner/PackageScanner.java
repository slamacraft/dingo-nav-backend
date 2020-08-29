package com.dingdo.util.scanner;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

public class PackageScanner {

    private Set<Class<?>> classes = new HashSet<Class<?>>();

    private ClassLoader classLoader;

    /**
     * 无参构造方法，默认加载上下文类加载器。
     */
    public PackageScanner() {
        this(Thread.currentThread().getContextClassLoader());
    }

    /**
     * 指定类加载器的构造方法
     *
     * @param classLoader
     */
    public PackageScanner(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public PackageScanner findAllClasses(String packageName) throws ClassNotFoundException {
        return findAllClasses(packageName, item -> true);
    }

    public PackageScanner findAllClasses(String packageName, Predicate<Class<?>> fitler) throws ClassNotFoundException {
        getClasses(packageName);
        this.classes = this.classes.stream().filter(fitler).collect(Collectors.toSet());
        return this;
    }

    public void getClasses(String packageName) throws ClassNotFoundException {
        URL url = classLoader.getResource(packageName.replace('.', '/'));
        if (url == null) {
            throw new ClassNotFoundException("该包不存在：" + packageName);
        }

        String protocol = url.getProtocol();

        if ("file".equals(protocol)) {
            getLocalClasses(packageName);
        } else if ("jar".equals(protocol)) {
            getJarClasses(packageName);
        }
    }


    public void getLocalClasses(String packageName) {
        File file;
        try {
            // 由路径的uri创建文件
            file = new File(classLoader.getResource(packageName.replace('.', '/')).toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException("未能读取到资源" + packageName);
        }

        // 遍历路径下所有的文件与文件夹
        file.listFiles(fileItem -> {
            // 如果是文件夹，递归扫描
            if (fileItem.isDirectory()) {
                if (packageName.length() == 0) {
                    getLocalClasses(fileItem.getName());
                } else {
                    getLocalClasses(packageName + "." + fileItem.getName());
                }
            }
            // 如果是文件，尝试加载.class文件
            if (fileItem.getName().endsWith(".class")) {
                try {
                    this.classes.add(classLoader.loadClass(packageName + "." + fileItem.getName().replace(".class", "")));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                return true;
            }
            return false;
        });
    }


    public void getJarClasses(String packageName) {
        String pathName = packageName.replace(".", "/");
        JarFile jarFile;
        try {
            URL url = classLoader.getResource(pathName);

            JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection();
            jarFile = jarURLConnection.getJarFile();
        } catch (IOException e) {
            throw new RuntimeException("未找到策略资源");
        }

        Enumeration<JarEntry> jarEntries = jarFile.entries();
        while (jarEntries.hasMoreElements()) {
            JarEntry jarEntry = jarEntries.nextElement();
            String jarEntryName = jarEntry.getName();

            if (jarEntryName.contains(pathName) && !jarEntryName.equals(pathName + "/")) {
                //递归遍历子目录
                if (jarEntry.isDirectory()) {
                    String clazzName = jarEntry.getName().replace("/", ".");
                    int endIndex = clazzName.lastIndexOf(".");
                    String prefix = null;
                    if (endIndex > 0) {
                        prefix = clazzName.substring(0, endIndex);
                    }
                    getJarClasses(prefix);
                }
                if (jarEntry.getName().endsWith(".class")) {
                    Class<?> clazz = null;
                    try {
                        clazz = classLoader.loadClass(jarEntry.getName().replace("/", ".").replace(".class", ""));
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    //判断，如果符合，添加
                    if (clazz != null) {
                        this.classes.add(clazz);
                    }
                }
            }
        }
    }

    public Set<Class<?>> getClasses() {
        return classes;
    }
}
