package com.dingdo.extendService.model.weather;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class WeatherHourly {

    private String time;    // 预报时间，格式yyyy-MM-dd hh:mm 	2013-12-30 13:00
    private String tmp; // 温度 	2
    private String cond_code;   // 天气状况代码 	101
    private String cond_txt;    // 天气状况描述 	多云
    private String wind_deg;    // 风向360角度 	290
    private String wind_dir;    // 风向 	西北
    private String wind_sc;     // 风力 	3-4
    private String wind_spd;    //  风速，公里/小时 	15
    private String hum;         // 相对湿度 	30
    private String pres;        // 大气压强 	1030
    private String pop;         //  降水概率，百分比 	30
    private String dew;         // 露点温度 	12
    private String cloud;       // 云量 	23
}
