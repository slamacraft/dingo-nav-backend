package com.dingdo.extendService.model.weather;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class WeatherUpdate {
    /**
     * 当地时间，24小时制，格式yyyy-MM-dd HH:mm     	2017-10-25 12:34
     */
    private String loc;
    /**
     * UTC时间，24小时制，格式yyyy-MM-dd HH:mm 	2017-10-25 04:34
     */
    private String utc;
}
