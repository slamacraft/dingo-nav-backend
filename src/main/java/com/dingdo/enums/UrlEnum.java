package com.dingdo.enums;

import java.util.Objects;

public enum UrlEnum {

    URL("http://127.0.0.1:8080", "HTTP服务器地址"),

    /*===============================================思知机器人============================================*/
    SI_ZHI_API("https://api.ownthink.com/bot", "思知机器人api地址"), //思知机器人api地址
    APPID("cebaf94c551f180d5c6847cf1ccaa1fa", "思知机器人的appid"),
    GET_IMAGE("/get_image", "获取图片"),

    /*===============================================百度链接============================================*/
    BAI_KE("https://baike.baidu.com/item/", "百度百科链接"),
    BAIDU_ZHIDAO("https://zhidao.baidu.com/search?lm=0&rn=10&pn=0&fr=search&ie=gbk&word=", "百度知道链接"),

    /*===============================================wiki============================================*/
    WIKI_PEDIA("http://wikipedia.moesalih.com/", "wiki百科"),
    GBF_WIKI("http://gbf.huijiwiki.com/index.php", "碧蓝幻想wiki地址"),

    /*===============================================音乐api============================================*/
    QQ_MUSIC_SEARCH("https://c.y.qq.com/soso/fcgi-bin/client_search_cp?ct=24&qqmusic_ver=1298&new_json=1&remoteplace=txt.yqq.center&searchid=46307684634141409&t=0&aggr=1&cr=1&catZhida=1&lossless=0&flag_qc=0&p=1&n=10&w=", "qq音乐点歌"),
    QQ_MUSIC_SONG("https://y.qq.com/n/yqq/song/", "QQ音乐歌曲界面"),
    MUSIC_163_SEARCH("https://v1.alapi.cn/api/music/search?keyword=", "网易云音乐搜索"),
    MUSIC_163_SONG("http://music.163.com/song/media/outer/url?id=", "网易云音乐直接地址"),
    MUSIC_163_RAND_MUSIC("https://api.uomg.com/api/rand.music?sort=热歌榜&format=json", "网易云音乐随机点歌"),

    /*===============================================和风天气api=================================================*/
    WEATHER("https://free-api.heweather.net/s6/weather/", "和风天气api地址"),
    WEATHER_NOW("now?", "目前气温详情"),
    WEATHER_DAILY_FORECAST("forecast?", "天气预报"),
    WEATHER_LIFESTYLE("lifestyle?", "生活指数查询"),
    WEATHER_HOURLY("hourly?", "逐时气温"),
    WEATHER_KEY("key=bc3498cfb4f14194904684fcfce4b58c&", "和风天气key"),

    /*===============================================python后端api=================================================*/
    PYTHON("http://47.112.225.39:8000", "python后端地址"),
    PYTHON_WDSR("predict_by_util/", "超分辨率接口");

    private String url;
    private String describe;

    UrlEnum(String url, String describe) {
        this.url = url;
        this.describe = describe;
    }

    public static UrlEnum getEnumByWeatherAPIType(String type) {
        UrlEnum[] values = UrlEnum.values();
        for (UrlEnum item : values) {
            if (Objects.equals(item.getUrl(), type + "?")) {
                return item;
            }
        }
        return null;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    @Override
    public String toString() {
        return url;
    }
}
