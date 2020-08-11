package com.dingdo.extendService.weatherService.impl;

import com.dingdo.extendService.weatherService.AbstractWeatherService;
import com.dingdo.extendService.weatherService.WeatherService;

import com.dingdo.model.msgFromMirai.ReqMsg;
import com.dingdo.model.msgFromMirai.ReqMsg;
import com.dingdo.model.weather.Weather;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WeatherServiceImpl extends AbstractWeatherService implements WeatherService {

    @Override
    public String sendReply(ReqMsg reqMsg) {
        StringBuffer reply = new StringBuffer();
        List<String> loactionList = getLoaction(reqMsg.getMessage());
        if (CollectionUtils.isEmpty(loactionList)){
            return "至少告诉我是哪个地方吧啊喂！";
        }
        for (String location:loactionList){
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
