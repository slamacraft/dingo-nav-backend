package com.dingdo.common.util.nlp;

/**
 * 一些声明信息
 *
 * @author slamacraft
 * @Description:
 * @date: 2020/8/29 14:28
 * @since JDK 1.8
 */
public enum ChiNumEnum {
    ZERO('零', 0),
    ONE('一', 1),
    TWO('二', 2),
    THREE('三', 3),
    FOUR('四', 4),
    FIVE('五', 5),
    SIX('六', 6),
    SEVEN('七', 7),
    EIGHT('八', 8),
    NINE('九', 9),

    TEN('十', 10),
    HUNDRED('百', 100),
    THOUSAND('千', 1000),
    TEN_THOUSAND('万', 10000),
    MILLION('兆', 1000000),
    BILLION('亿', 100000000),


    HALF('半', 30),
    RI('日', 7),
    TIAN('天', 7),

    ;

    private final char chi;
    private final int num;

    ChiNumEnum(Character chi, int num) {
        this.chi = chi;
        this.num = num;
    }

    public static boolean isChiNum(char c) {
        ChiNumEnum[] values = ChiNumEnum.values();
        for (ChiNumEnum value : values) {
            if (value.chi == c) {
                return true;
            }
        }
        return false;
    }

    public static int getNumByChi(char c) {
        ChiNumEnum[] values = ChiNumEnum.values();
        for (ChiNumEnum value : values) {
            if (value.chi == c) {
                return value.num;
            }
        }
        throw new RuntimeException("无效的中文数字");
    }
}
