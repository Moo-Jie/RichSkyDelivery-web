package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/dish")
@Slf4j
@Api(tags = "管理端-菜品相关接口")
public class DishController {
    @Autowired
    private DishService dishService;

    /**
     * 添加菜品
     *
 * @param dishDTO
     * @return com.sky.result.Result
     * @author DuRuiChi
     * @create 2024/10/25
     **/
    @PostMapping
    @ApiOperation("添加菜品")
    public Result save(@RequestBody DishDTO dishDTO) {
        log.info("添加菜品:{}", dishDTO);
        dishService.save(dishDTO);
        return Result.success();
    }

    /**
     * 菜品分页查询
     *
 * @param pageDTO
     * @return com.sky.result.Result
     * @author DuRuiChi
     * @create 2024/10/25
     **/
    @GetMapping("page")
    @ApiOperation("菜品分页查询")
    public Result page(DishPageQueryDTO pageDTO) {
        log.info("菜品分页查询:{}", pageDTO);
        return Result.success(dishService.page(pageDTO));
    }

    /**
     * 批量删除菜品
     *
 * @param ids
     * @return com.sky.result.Result
     * @author DuRuiChi
     * @create 2024/10/25
     **/
    @DeleteMapping
    @ApiOperation("批量删除菜品")
    public Result delete(@RequestParam List<Long> ids) {
        log.info("批量删除菜品:{}", ids);
        dishService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 根据菜品分类的id查询菜品
     *
 * @param categoryId
     * @return com.sky.result.Result
     * @author DuRuiChi
     * @create 2024/10/25
     **/
    @GetMapping("list")
    @ApiOperation("根据菜品分类的id查询菜品")
    public Result list(Integer categoryId) {
        log.info("根据分类id查询菜品:{}", categoryId);
        return Result.success(dishService.selectByCategoryId(categoryId));
    }

    /**
     * 根据id查询菜品
     *
 * @param id
     * @return com.sky.result.Result<com.sky.vo.DishVO>
     * @author DuRuiChi
     * @create 2024/10/25
     **/
    @GetMapping("/{id}")
    @ApiOperation("根据id查询菜品")
    public Result<DishVO> getDishById(@PathVariable Long id) {
        log.info("根据id查询菜品:{}", id);
        return Result.success(dishService.getDishById(id));
    }


    /**
     * 修改菜品
     *
 * @param dishDTO
     * @return com.sky.result.Result
     * @author DuRuiChi
     * @create 2024/10/25
     **/
    @PutMapping
    @ApiOperation("修改菜品")
    public Result update(@RequestBody DishDTO dishDTO) {
        log.info("修改菜品:{}", dishDTO);
        dishService.update(dishDTO);
        return Result.success();
    }

    /**
     * 菜品起售停售
     *
 * @param status
 * @param id
     * @return com.sky.result.Result
     * @author DuRuiChi
     * @create 2024/10/25
     **/
    @PostMapping("status/{status}")
    @ApiOperation("菜品起售停售")
    public Result startOrStop(@PathVariable Integer status, Long id) {
        log.info("菜品起售停售:{}", status, id);
        dishService.startOrStop(status, id);
        return Result.success();
    }
}
