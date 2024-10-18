package com.sky.utils;

import com.sky.properties.AliOSSProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;


@AutoConfiguration
@EnableConfigurationProperties(AliOSSProperties.class)//指定oss连接参数配置类参与Bean的注入
public class AliOSSAutoConfiguration {
    //指定oss工具类参与Bean的注入
    @Bean
    @ConditionalOnMissingBean
    public AliOSSUtils aliOSSUtils(AliOSSProperties aliOSSProperties) {
        return new AliOSSUtils(aliOSSProperties);
    }
}
