package com.dingdo.extendService.model.weather;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class WeatherNow {
    private String fl;  //  体感温度，默认单位：摄氏度 	23
    private String tmp; // 温度，默认单位：摄氏度 	21
    private String cond_code;   //  	实况天气状况代码 	100
    private String cond_txt;    // 实况天气状况描述 	晴
    private String wind_deg;    // 风向360角度 	305
    private String wind_dir;    //  风向 	西北
    private String wind_sc; // 风力 	3-4
    private String wind_spd;    // 风速，公里/小时 	15
    private String hum; // 相对湿度 	40
    private String pcpn;    // 降水量 	0
    private String pres;    // 大气压强 	1020
    private String vis; // 能见度，默认单位：公里 	10
    private String cloud;   // 云量 	23

    @Override
    public String toString() {
        return "现在的天气状况：" +
                "体感温度" + fl + "℃" +
                "，气温" + tmp + "℃" +
                "，" + cond_txt  +
                "，" + wind_dir + wind_sc + "级" +
                "，相对湿度" + hum + "%" +
                "，降水量" + pcpn + "毫米" +
                "，大气压强" + pres + "帕" +
                "，能见度" + vis + "公里" +
                "，云量" + cloud + "\n" ;
    }
}
