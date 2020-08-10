package com.example.demo.extendService.weatherService.impl;

import com.example.demo.extendService.weatherService.AbstractWeatherService;
import com.example.demo.extendService.weatherService.WeatherLifestyleService;
import com.example.demo.model.msgFromCQ.ReceiveMsg;
import com.example.demo.model.msgFromCQ.ReplyMsg;
import com.example.demo.model.weather.Weather;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WeatherLifestyleServiceImpl extends AbstractWeatherService implements WeatherLifestyleService {

    @Override
    public ReplyMsg sendReply(ReceiveMsg receiveMsg) {
        ReplyMsg replyMsg = new ReplyMsg();

        StringBuffer reply = new StringBuffer();
        List<String> loactionList = getLoaction(receiveMsg.getRaw_message());
        if (CollectionUtils.isEmpty(loactionList)) {
            replyMsg.setReply("请问你要查询哪个地方呢");
            return replyMsg;
        }
        for (String location : loactionList) {
            Weather weatherInfoFromApi = getWeatherInfoFromApi(location, "lifestyle");
            reply.append(location);
            reply.append(weatherInfoFromApi.getHeWeather6().get(0).getLifeStyleInfo());
        }

        replyMsg.setReply(reply.toString());
        return replyMsg;
    }

    @Override
    public String getReply(ReceiveMsg receiveMsg) {
        return null;
    }
}