package com.example.demo.util;

/**
 * 打印进度条的工具类
 */
public class PrintBarUtil {

    /**
     * 打印进度条
     *
     * @param pringTip 打印进度条时候的提示
     * @param index    当前的下标
     * @param size     当前循环的总量
     */
    public static void print(String pringTip, int index, int size) {
        print(pringTip, index, size, size / 10);
    }

    /**
     * 打印进度条
     *
     * @param pringTip 打印进度条时候的提示
     * @param index    当前的下标
     * @param size     当前循环的总量
     * @param section  每隔多少下标打印一次进度条
     */
    public static void print(String pringTip, int index, int size, int section) {
        if (section > size) {
            section = size;
        }

        double progress = (index + 1) * 1.0 / size * 100;
        if ((index + 1) % section == 0 || index == size - 1) {
            printBar(pringTip, progress);
        }
    }

    /**
     * 打印一条进度条
     *
     * @param pringTip 打印提示
     * @param progress 进度
     */
    public static void printBar(String pringTip, double progress) {
        StringBuffer printBuffer = new StringBuffer();
        int bar = (int) progress;
        bar = bar - bar / 100 * 100;
        int bigBar = bar / 10;
        int emptyBar = 10 - bigBar - 1;

        if (progress == 100) {
            System.out.println("\033[33m" + pringTip + ":[==========](100.0%)(完成)\033[37m");
            return;
        }

        printBuffer.append(pringTip + ":[");
        for (int i = 0; i < bigBar; i++) {
            printBuffer.append('=');
        }
        printBuffer.append('>');
        for (int i = 0; i < emptyBar; i++) {
            printBuffer.append('-');
        }
        printBuffer.append(']');
        System.out.printf("\033[33m" + printBuffer + "(%2.1f%%)\033[37m\n", progress);
    }
}
