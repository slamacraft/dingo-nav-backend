package com.dingo.service.model.weather;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

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
            if (!lifestyle.isEmpty()) {
                lifestyle.forEach(item -> {
                    result.append(item.toString() + "\n");
                });
            }
            return result.toString();
        }
    }
}
