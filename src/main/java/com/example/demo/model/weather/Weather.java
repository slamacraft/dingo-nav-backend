package com.example.demo.model.weather;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Weather {

    @JsonProperty("heWeather6")
    private List<HeWeather6> heWeather6 = new ArrayList<>();

    public Weather() {
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public class HeWeather6 {
        private WeatherBasic basic;
        private WeatherUpdate update;
        private String status;
        private WeatherNow now;
        private List<WeatherDailyForecast> daily_forecast = new ArrayList<>();
        private List<WeatherLifestyle> lifestyle = new ArrayList<>();
        private List<WeatherHourly> hourly = new ArrayList<>();

        public String getLifeStyleInfo() {
            StringBuffer result = new StringBuffer();
            if (CollectionUtils.isNotEmpty(lifestyle)) {
                lifestyle.forEach(item -> {
                    result.append(item.toString() + "\n");
                });
            }
            return result.toString();
        }
    }
}
