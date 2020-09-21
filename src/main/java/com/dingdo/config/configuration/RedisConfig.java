package com.dingdo.config.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    /**
     * 注册一个redis模板类
     * @param factory
     * @return
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        //关联
        template.setConnectionFactory(factory);
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        //设置key的序列化器
        template.setKeySerializer(stringRedisSerializer);
        template.setHashKeySerializer(stringRedisSerializer);
        //设置value的序列化器
        template.setValueSerializer(stringRedisSerializer);
        template.setHashValueSerializer(stringRedisSerializer);

        return template;
    }

    @Bean
    public RedisTemplate<String, Boolean> userStautsRedisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Boolean> template = new RedisTemplate<>();
        //关联
        template.setConnectionFactory(factory);
        //设置key的序列化器
        template.setKeySerializer(new StringRedisSerializer());
        //设置value的序列化器
        template.setValueSerializer(new StringRedisSerializer());
        return template;
    }
}
