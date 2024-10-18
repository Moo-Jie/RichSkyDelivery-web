package com.sky.utils;

import com.aliyun.oss.AliOSSUtils;
import com.sky.entity.AliOSSProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;


@EnableConfigurationProperties(AliOSSProperties.class)
@AutoConfiguration
public class AliOSSAutoConfiguration {
    @Bean
    public AliOSSUtils aliOSSUtils(AliOSSProperties aliOSSProperties) {
        return new AliOSSUtils(aliOSSProperties);
    }
}
