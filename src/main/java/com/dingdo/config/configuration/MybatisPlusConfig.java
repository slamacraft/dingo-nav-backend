package com.dingdo.config.configuration;

import com.baomidou.mybatisplus.core.incrementer.IKeyGenerator;
import com.baomidou.mybatisplus.extension.incrementer.H2KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * mybatis配置类
 */
@Configuration
public class MybatisPlusConfig {

    /**
     * mybatis-plus主键生成器
     *
     * @return {@link IKeyGenerator}
     */
    @Bean
    public IKeyGenerator keyGenerator() {
        return new H2KeyGenerator();
    }

}
