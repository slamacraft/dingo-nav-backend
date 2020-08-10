package com.example.demo.extendService.weatherService.impl;

import com.example.demo.extendService.weatherService.AbstractWeatherService;
import com.example.demo.extendService.weatherService.WeatherService;
import com.example.demo.model.msgFromCQ.ReceiveMsg;
import com.example.demo.model.msgFromCQ.ReplyMsg;
import com.example.demo.model.weather.Weather;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WeatherServiceImpl extends AbstractWeatherService implements WeatherService {

    @Override
    public ReplyMsg sendReply(ReceiveMsg receiveMsg) {
        ReplyMsg replyMsg = new ReplyMsg();

        StringBuffer reply = new StringBuffer();
        List<String> loactionList = getLoaction(receiveMsg.getRaw_message());
        if (CollectionUtils.isEmpty(loactionList)){
            replyMsg.setReply("至少告诉我是哪个地方吧啊喂！");
            return replyMsg;
        }
        for (String location:loactionList){
            Weather weatherInfoFromApi = getWeatherInfoFromApi(location, "now");
            reply.append(location);
            reply.append(weatherInfoFromApi.getHeWeather6().get(0).getNow().toString());
        }

        replyMsg.setReply(reply.toString());
        return replyMsg;
    }

    @Override
    public String getReply(ReceiveMsg receiveMsg) {
        return null;
    }
}
