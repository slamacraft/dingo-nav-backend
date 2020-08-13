package com.dingdo.config;

import com.dingdo.Listener.ApplicationRunListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class VarConfig {

    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(3000);// 设置超时
        requestFactory.setReadTimeout(3000);
        return new RestTemplate(requestFactory);
    }

    @Bean
    public ApplicationRunListener applicationStartListener(){
        return new ApplicationRunListener();
    }
}
