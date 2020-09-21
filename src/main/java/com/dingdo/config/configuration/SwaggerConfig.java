package com.dingdo.config.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author slamacraft
 * @version 1.0
 * @date 2020/9/21 8:45
 * @since JDK 1.8
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {

    private ApiInfo apiInfo(){
        return new ApiInfoBuilder().title("机器人api")
                .description("qq机器人调用api接口")
                .version("1.0.0")
                .build();
    }

    // 核心配置信息
    @Bean
    public Docket createRestApi(){
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.dingdo.msgHandler.controller"))
                .paths(PathSelectors.any())
                .build();
    }

}
