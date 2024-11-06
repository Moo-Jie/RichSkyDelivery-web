package com.sky.controller.user;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.result.Result;
import com.sky.service.ShoppingCartService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/user/shoppingCart")
@Slf4j
@Api(tags = "C端-购物车相关接口")
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;

    @PostMapping("/add")
    @ApiOperation("添加购物车")
    /**
     * 添购物车功能
     *
 * @param shoppingCartDTO
     * @return com.sky.result.Result<java.lang.String>
     * @author DuRuiChi
     * @create 2024/10/30
     **/
    public Result<String> add(@RequestBody ShoppingCartDTO shoppingCartDTO) {
        log.info( "添加购物车");
        shoppingCartService.addShoppingCart(shoppingCartDTO);
        return Result.success();
    }

    /**
     * 查看购物车功能
     *
     * @return com.sky.result.Result<java.util.List<com.sky.entity.ShoppingCart>>
     * @author DuRuiChi
     * @create 2024/10/31
     **/
    @GetMapping("list")
    @ApiOperation("查看购物车")
    public Result<List<ShoppingCart>> list() {
        log.info("查看购物车");
        List<ShoppingCart> shoppingCartList = shoppingCartService.showShoppingCart();
        return Result.success(shoppingCartList);
    }

    /**
     *  清空购物车功能
     *
     * @return com.sky.result.Result<java.lang.String>
     * @author DuRuiChi
     * @create 2024/10/31
     **/
    @DeleteMapping("/clean")
    @ApiOperation("清空购物车")
    public Result<String> clean() {
        shoppingCartService.cleanShoppingCart();
        return Result.success();
    }

    @PostMapping("/sub")
    @ApiOperation("减少购物车中菜品数量")
    public Result<String> sub(@RequestBody ShoppingCartDTO shoppingCartDTO) {
        shoppingCartService.subShoppingCart(shoppingCartDTO);
        return Result.success();
    }
}
