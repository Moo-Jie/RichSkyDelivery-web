package com.sky.controller.user;

import com.sky.service.testService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/getCapitalFirstLetters")
@Slf4j
@Api(tags = "测试端口")
public class TestContorller {
    @Autowired
    private testService service;

    @GetMapping
    public String getCapitalLetters(String str) {
        log.info("转换为大写字母:" + str);
//        String CapitalLetters=str.substring(0, 1).toUpperCase() + str.substring(1)
        String CapitalLetters=service.getCapitalLetters(str);
        log.info("转换成功:" + CapitalLetters);
        return CapitalLetters;
    }
}
