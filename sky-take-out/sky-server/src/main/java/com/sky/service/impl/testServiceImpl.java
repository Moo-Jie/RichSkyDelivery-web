package com.sky.service.impl;

import com.sky.service.testService;
import org.springframework.stereotype.Service;

@Service
public class testServiceImpl implements testService {
    /**
     * @return
     */
    @Override
    public String getCapitalLetters(String str) {
        //转换为大写字母
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
