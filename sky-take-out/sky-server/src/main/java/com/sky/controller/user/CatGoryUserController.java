package com.sky.controller.user;

import com.sky.entity.Category;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import com.sky.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user/category")
@Slf4j
@Api(tags = "C端-分类浏览接口")
public class CatGoryUserController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 分类浏览
     * @param type
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("分类浏览")
    public Result<List<Category>> list(Integer type){
        log.info("分类浏览");
        return Result.success(categoryService.list(type));
    }
}
