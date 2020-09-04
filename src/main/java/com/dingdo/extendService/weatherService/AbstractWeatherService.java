package com.dingdo.extendService.weatherService;

import com.alibaba.fastjson.JSONObject;
import com.dingdo.enums.UrlEnum;
import com.dingdo.extendService.model.weather.Weather;
import com.dingdo.util.NLPUtils;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractWeatherService{

    @Autowired
    private RestTemplate restTemplate;

    private static Segment segment = NLPUtils.getPlaceSegment();

    /**
     * 从文本中获取地名
     * @param msg
     */
    public static List<String> getLocation(String msg){
        List<Term> termList = segment.seg(msg);

        List<String> result = new ArrayList<>();
        for (Term term : termList){
            if (term.nature.toString().equals("ns")){
                result.add(term.word);
            }
        }

        return result;
    }

    protected Weather getWeatherInfoFromApi(String location, String type) {
        Weather weather = new Weather();

        String url = UrlEnum.WEATHER.getUrl() + UrlEnum.getEnumByWeatherAPIType(type) + UrlEnum.WEATHER_KEY;
        url += "location=" + location;

        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            weather = JSONObject.parseObject(response.getBody(), Weather.class);
        } catch (Exception e) {
            System.out.println("api请求中断");
            e.printStackTrace();
        }

        return weather;
    }

}
