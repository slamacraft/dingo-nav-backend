package com.dingdo.extendService.musicService.enums;

import java.util.Random;

/**
 * @author slamacraft
 * @version 1.0
 * @date 2020/9/23 9:20
 * @since JDK 1.8
 */
public enum  MusicPreviewUrlEnums {
    ZHIHU_IMAGE_1("https://pic1.zhimg.com/80/v2-689935e90ad742b0657cfe26db625954_720w.jpg?source=1940ef5c", "到点了"),
    ZHIHU_IMAGE_2("https://pic2.zhimg.com/80/v2-a069e19b50660410b414101dc9a48d40_720w.jpg?source=1940ef5c", "到点了"),
    ZHIHU_IMAGE_3("https://pic1.zhimg.com/80/v2-deeeedcd7bff430ad1fbf9c838f41ef1_720w.jpg?source=1940ef5c", "到点了"),
    ZHIHU_IMAGE_4("https://pic4.zhimg.com/80/v2-4cf9f6368e6cf039af030a74471c9caa_720w.jpg?source=1940ef5c", "到点了"),
    ZHIHU_IMAGE_5("https://pic3.zhimg.com/80/v2-b81bcc06ede351b485304c24e7df7b7f_720w.jpg?source=1940ef5c", "到点了"),
    ZHIHU_IMAGE_6("https://pic3.zhimg.com/80/v2-1ef7199992ab608096ccd4819cfa4924_720w.jpg?source=1940ef5c", "到点了"),
    ZHIHU_IMAGE_7("https://pic1.zhimg.com/80/v2-493c7a878a92d31d966f6599bfc4f8cc_720w.jpg?source=1940ef5c", "到点了"),
    ZHIHU_IMAGE_8("https://pic2.zhimg.com/80/v2-5b12c636cc4e016e94a6c6a56dc4d12d_720w.jpg?source=1940ef5c", "到点了"),
    ZHIHU_IMAGE_9("https://pic2.zhimg.com/80/v2-9137a9146ecb3cd6e5f89e14bbe4414e_720w.jpg?source=1940ef5c", "到点了"),
    ZHIHU_IMAGE_10("https://pic4.zhimg.com/80/v2-fb8df3a53a946f894e677d2616d04e73_720w.jpg?source=1940ef5c", "到点了");


    private String url;
    private String desc;
    private static Random random = new Random();

    MusicPreviewUrlEnums(String url, String desc) {
        this.url = url;
        this.desc = desc;
    }

    public static String getRandomUrl(){
        MusicPreviewUrlEnums[] values = values();
        int randomInt = random.nextInt(values.length);
        return values[randomInt].getUrl();
    }

    public String getUrl() {
        return url;
    }
}
