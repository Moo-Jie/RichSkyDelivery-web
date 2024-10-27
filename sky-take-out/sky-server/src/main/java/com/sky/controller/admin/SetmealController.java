package com.sky.controller.admin;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/setmeal")
@Slf4j
@Api(tags = "管理端-套餐管理相关接口")
public class SetmealController {
    @Autowired
    private SetmealService setmealService;

    /**
     * @param setmealPageQueryDTO
     * @return com.sky.result.Result<com.sky.result.PageResult>
     * @author DuRuiChi
     * @create 2024/10/25
     **/
    @GetMapping("/page")
    @ApiOperation("套餐分页查询")
    public Result<PageResult> page(SetmealPageQueryDTO setmealPageQueryDTO){
        log.info("套餐分页查询，参数：{}",setmealPageQueryDTO);
        return Result.success(setmealService.pageQuery(setmealPageQueryDTO));
    }

    /**
     *
     *
     * @param setmealDTO
     * @return com.sky.result.Result
     * @author DuRuiChi
     * @create 2024/10/25
     **/
    @PostMapping
    @ApiOperation("新建套餐")
    public Result save(@RequestBody SetmealDTO setmealDTO){
        log.info("新建套餐，参数：{}",setmealDTO);
        setmealService.save(setmealDTO);
        return Result.success();
    }

    /**
     *
     *
     * @param ids
     * @return com.sky.result.Result
     * @author DuRuiChi
     * @create 2024/10/25
     **/
    @DeleteMapping
    @ApiOperation("删除套餐")
    public Result delete(@RequestParam List<Long> ids){
        log.info("删除套餐，参数：{}",ids);
        setmealService.deleteByIds(ids);
        return Result.success();
    }

    /**
     *
     *
     * @param id
     * @return com.sky.result.Result<com.sky.vo.SetmealVO>
     * @author DuRuiChi
     * @create 2024/10/25
     **/
    @GetMapping("/{id}")
    @ApiOperation("根据id查询套餐")
    public Result<SetmealVO> getById(@PathVariable Long id){
        log.info("根据id查询套餐，参数：{}",id);
        return Result.success(setmealService.getById(id));
    }

    /**
     *
     *
     * @param setmealDTO
     * @return com.sky.result.Result
     * @author DuRuiChi
     * @create 2024/10/25
     **/
    @PutMapping
    @ApiOperation("修改套餐")
    public Result update(@RequestBody SetmealDTO setmealDTO){
        log.info("修改套餐，参数：{}",setmealDTO);
        setmealService.update(setmealDTO);
        return Result.success();
    }

    /**
     *
     *
 * @param status
 * @param id
     * @return com.sky.result.Result
     * @author DuRuiChi
     * @create 2024/10/25
     **/
    @PostMapping("/status/{status}")
    @ApiOperation("套餐起售停售")
    public Result startOrStop(@PathVariable Integer status,Integer id){
        log.info("套餐起售停售，参数：{}",id);
        setmealService.startOrStop(status,id);
        return Result.success();
    }
}
