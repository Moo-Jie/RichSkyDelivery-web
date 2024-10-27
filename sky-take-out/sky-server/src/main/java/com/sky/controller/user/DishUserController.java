package com.sky.controller.user;

import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/user/dish")
@Api(tags = "C端-菜品浏览接口")
@Slf4j
public class DishUserController {
    @Autowired
    private DishService dishService;

    /**
     * 菜品浏览
     *
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    public Result<List<DishVO>> list(@RequestParam Integer categoryId) {
        log.info("菜品浏览, 分类ID为：{}", categoryId);
        return Result.success(dishService.selectByCategoryId(categoryId));
    }
}