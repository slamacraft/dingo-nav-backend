package com.dingdo.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.boot.system.ApplicationHome;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

/**
 * 文件操作工具类
 * 可以加载文件、将字符添加到文件末尾
 */
public class FileUtil {

    // 使用log4j打印日志
    private static Logger logger = Logger.getLogger(FileUtil.class);

    /**
     * 获取jar路径的静态内部类
     */
    private static class JarPathUtil {
        private static String jarUrl;

        static {
            ApplicationHome home = new ApplicationHome(FileUtil.class);
            File jarFile = home.getSource();
            if (jarFile == null || StringUtils.isBlank(jarFile.getParentFile().toString())) {
                jarUrl = "/python/CQPython/static";
            } else {
                jarUrl = jarFile.getParentFile().toString();
            }
            System.out.println("获取的jar包路径为:" + jarUrl);
        }
    }


    /**
     * 将文件读取为String
     *
     * @param path
     * @return
     */
    public static String loadFileFromResourse(String path) {
        StringBuffer result = new StringBuffer();
        BufferedReader br = null;
        try {
            InputStream stream = FileUtil.class.getClassLoader().getResourceAsStream(path);
            br = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
            String s = "";
            while ((s = br.readLine()) != null) {
                result.append(s + "\n");
            }
            br.close();
        } catch (Exception e) {
            logger.error(e);
        }
        return result.toString();
    }

    /**
     * 将文件读取为String
     *
     * @param path
     * @return
     */
    public static String loadFile(String path) {
        File file = new File(path);
        StringBuffer result = new StringBuffer();

        if (file.isFile() && file.exists()) { //判断文件是否存在
            try {
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(new FileInputStream(file), "UTF-8"));//构造一个BufferedReader类来读取文件
                String s = null;
                while ((s = br.readLine()) != null) {//使用readLine方法，一次读一行
                    result.append(System.lineSeparator() + s);
                }
                br.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result.toString();
        } else {
            logger.info("找不到指定的文件，请确认文件路径是否正确");
        }

        return result.toString();
    }

    /**
     * 将文件读取为String
     *
     * @param path
     * @return
     */
    public static String loadFileFromJarPath(String path) {
        path = JarPathUtil.jarUrl + "/message/" + path;
        return loadFile(path);
    }

    /**
     * 将文本写入文件末尾
     *
     * @param path 相对于jar包同级的目录为根目录
     * @param text
     */
    public static void saveMsgToFile(String path, String text) {
        // 获取当前jar包所在的文件路径
        path = JarPathUtil.jarUrl + "/message/" + path;
        try {
            writeFile(path, text);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将文本写入文件末尾
     *
     * @param path 相对于jar包同级的目录为根目录
     * @param text
     */
    public static void writeFile(String path, String text) throws IOException {
        if (StringUtils.isBlank(text)) {
            return;
        }
        File file = new File(path);// 要写入的文件路径
        if (!file.exists()) {// 判断文件是否存在
            try {
                file.createNewFile();// 如果文件不存在创建文件
            } catch (IOException e) {
                logger.error(e);
            }
        }

        FileOutputStream fos = null;
//        PrintStream ps = null;
        Writer writer = null;
        try {
            fos = new FileOutputStream(file, true);// 文件输出流 追加
            writer = new BufferedWriter(new OutputStreamWriter(fos, "UTF-8"));
            writer.write(text);
        } catch (FileNotFoundException e) {
            logger.error(e);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } finally {
            writer.flush();
            writer.close();
        }

        System.out.println("文件" + path + "写入完毕!");
    }

    /**
     * 清空文件
     *
     * @param filePath
     */
    public static void clearFile(String filePath) {
        File file = new File(filePath);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write("");
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 保存图片
     *
     * @param image    图片
     * @param fileName 图片名称
     * @return
     */
    public static String saveImage(BufferedImage image, String fileName) {
        File file = new File(JarPathUtil.jarUrl + "/image/" + fileName + ".jpg");
        if (!file.exists()) {
            try {
                file.createNewFile();
                ImageIO.write(image, "jpg", file);
                System.out.println("图片保存至:" + JarPathUtil.jarUrl + "/image/" + fileName + ".jpg");
                return JarPathUtil.jarUrl + "/image/" + fileName + ".jpg";
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            return JarPathUtil.jarUrl + "/image/" + fileName + ".jpg";
        }
        return null;
    }


    public static String getImagePath(String imageName) {
        return JarPathUtil.jarUrl + "/image/" + imageName + ".jpg";
    }
}
