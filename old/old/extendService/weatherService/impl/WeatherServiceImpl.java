package com.example.old.extendService.weatherService.impl;

import com.dingdo.extendService.model.weather.Weather;
import com.example.old.extendService.weatherService.AbstractWeatherService;
import com.example.old.extendService.weatherService.WeatherService;
import com.dingdo.msgHandler.model.ReqMsg;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WeatherServiceImpl extends AbstractWeatherService implements WeatherService {

    @Override
    public String sendReply(ReqMsg reqMsg) {
        StringBuilder reply = new StringBuilder();
        List<String> locationList = getLocation(reqMsg.getRawMessage());
        if (CollectionUtils.isEmpty(locationList)){
            return "至少告诉我是哪个地方吧啊喂！";
        }
        for (String location:locationList){
            Weather weatherInfoFromApi = getWeatherInfoFromApi(location, "now");
            reply.append(location);
            reply.append(weatherInfoFromApi.getHeWeather6().get(0).getNow().toString());
        }

        return reply.toString();
    }

    @Override
    public String getReply(ReqMsg reqMsg) {
        return null;
    }
}
