package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.ShopService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController("AdminShopController")//指定不同的Bean名来区分不同的ShopController类
@Slf4j
@Api(tags = "管理端-店铺相关接口")
@RequestMapping("/admin/shop")
public class ShopController {
    @Autowired
    private ShopService shopService;

    /**
     * 设置店铺状态
     *
     * @param status
     * @return com.sky.result.Result
     * @author DuRuiChi
     * @create 2024/10/25
     **/
    @PutMapping("/{status}")
    @ApiOperation("设置店铺状态")
    public Result setStatus(@PathVariable Integer status){
        log.info("设置店铺状态为{}",status==1?"营业中":"打烊中");
        shopService.setStatus(status);
        return Result.success();
    }

    /**
     * 获取店铺状态
     * 
     * @return com.sky.result.Result
     * @author DuRuiChi
     * @create 2024/10/25
     **/
    @GetMapping("/status")
    @ApiOperation("获取店铺状态")
    public Result getStatus(){
        Integer status = shopService.getStatus();
        log.info("获取到店铺状态为{}",status==1?"营业中":"打烊中");
        return Result.success(status);
    }
}
