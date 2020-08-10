package com.dingdo.model.weather;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class WeatherBasic {

    /**
     * 城市id
     */
    private String cid;
    /**
     * 城市名称
     */
    private String location;
    /**
     * 所属城市
     */
    private String parent_city;
    /**
     * 主管地区
     */
    private String admin_area;
    /**
     * 所属国家
     */
    private String cnty;
    /**
     * 经度
     */
    private String lat;
    /**
     * 维度
     */
    private String lon;
    /**
     * 时区
     */
    private String tz;
}
