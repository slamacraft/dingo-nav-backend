package com.example.demo.util;

import java.io.IOException;

/**
 * 服务启动工具类
 */
public class ServiceUtil {

    //------------------------------------启动mysql---------------------------------------
    public static void startMysql() {
        String command = "net start mysql4566";
        try {
            Process p = Runtime.getRuntime().exec(command);
            System.out.println("----------mysql服务启动成功----------");
        } catch (IOException e) {
            System.out.println("----------mysql服务启动失败----------");
            e.printStackTrace();
        }
    }
}
