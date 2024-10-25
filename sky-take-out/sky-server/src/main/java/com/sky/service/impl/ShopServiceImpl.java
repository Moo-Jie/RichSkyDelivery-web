package com.sky.service.impl;

import com.sky.service.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class ShopServiceImpl implements ShopService {
    public static final String KEY = "SHOP_STATUS";

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * @param status
     * @return void
     * @author DuRuiChi
     * @create 2024/10/25
     **/
    @Override
    public void setStatus(Integer status) {
        redisTemplate.opsForValue().set(KEY, status);
    }


    /**
     * @return java.lang.Integer
     * @author DuRuiChi
     * @create 2024/10/25
     **/
    @Override
    public Integer getStatus() {
        return (Integer)redisTemplate.opsForValue().get(KEY);
    }
}
