package com.dingo.extendService.weatherService.impl;

import com.dingo.extendService.model.weather.Weather;
import com.dingo.extendService.weatherService.AbstractWeatherService;
import com.dingo.extendService.weatherService.WeatherLifestyleService;
import com.dingo.msgHandler.model.ReqMsg;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WeatherLifestyleServiceImpl extends AbstractWeatherService implements WeatherLifestyleService {

    @Override
    public String sendReply(ReqMsg reqMsg) {
        StringBuilder reply = new StringBuilder();
        List<String> locationList = getLocation(reqMsg.getRawMessage());
        if (CollectionUtils.isEmpty(locationList)) {
            return "请问你要查询哪个地方呢";
        }
        for (String location : locationList) {
            Weather weatherInfoFromApi = getWeatherInfoFromApi(location, "lifestyle");
            reply.append(location);
            reply.append(weatherInfoFromApi.getHeWeather6().get(0).getLifeStyleInfo());
        }
        return reply.toString();
    }

    @Override
    public String getReply(ReqMsg reqMsg) {
        return null;
    }
}
