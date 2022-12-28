package com.dingo.service.model.weather;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class WeatherDailyForecast {

    private String date;    // 预报日期 	2013-12-30
    private String sr;  // 日出时间 	07:36
    private String ss;  //  日落时间 	16:58
    private String mr;  // 月升时间 	04:47
    private String ms;  // 月落时间 	14:59
    private String tmp_max; // 最高温度 	4
    private String tmp_min; // 最低温度 	-5
    private String cond_code_d; // 白天天气状况代码 	100
    private String cond_code_n; // 夜间天气状况代码 	100
    private String cond_txt_d; //  	白天天气状况描述 	晴
    private String cond_txt_n;  //  	晚间天气状况描述 	晴
    private String wind_deg;    // 风向360角度 	310
    private String wind_dir; //  	风向 	西北风
    private String wind_sc; // 风力 	1-2
    private String wind_spd;    // 风速，公里/小时 	14
    private String hum; //  	相对湿度 	37
    private String pcpn;    // 降水量 	0
    private String pop; // 降水概率 	0
    private String pres;    // 大气压强 	1018
    private String uv_index;    // 紫外线强度指数 	3
    private String vis; // 能见度，单位：公里 	10
}
