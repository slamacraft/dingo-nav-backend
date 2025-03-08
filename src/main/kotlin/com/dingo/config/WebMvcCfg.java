package com.dingo.config;

import com.dingo.config.interceptor.WhiteIpInterceptor;
import com.dingo.config.listener.ApplicationRunListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author slamacraft
 * @version 1.0
 * @date 2020/9/21 9:10
 * @since JDK 1.8
 */
@Configuration
public class WebMvcCfg implements WebMvcConfigurer {
    @Autowired
    private WhiteIpInterceptor whiteIpInterceptor;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/");
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjar/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");

        //        registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");
//        registry.addResourceHandler("/**").addResourceLocations("file:C://Users/Administrator/Desktop/");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 因为经过内网穿透后访问ip都是本机，所以没有用到白名单拦截
//        registry.addInterceptor(whiteIpInterceptor);
    }

    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(100 * 1000);// 设置超时
        requestFactory.setReadTimeout(100 * 1000);
        return new RestTemplate(requestFactory);
    }

    @Bean
    public ApplicationRunListener applicationStartListener() {
        return new ApplicationRunListener();
    }
}
