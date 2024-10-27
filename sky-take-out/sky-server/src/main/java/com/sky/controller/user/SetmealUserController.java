package com.sky.controller.user;

import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/setmeal")
@Slf4j
@Api(tags = "C端-套餐浏览接口")
public class SetmealUserController {
    @Autowired
    private SetmealService setmealService;

    @GetMapping("/list")
    @ApiOperation("套餐浏览")
    public Result<List<SetmealVO>> list(@RequestParam Integer categoryId){
        log.info("套餐浏览");
        return Result.success(setmealService.listBycategoryId(categoryId));
    }

    @GetMapping("/dish/{id}")
    @ApiOperation("根据套餐id查询菜品")
    public Result<List<DishItemVO>> dish(@PathVariable Integer id){
        log.info("根据套餐id查询菜品");
        return Result.success(setmealService.getDishBySetmealId(id));
    }

}
