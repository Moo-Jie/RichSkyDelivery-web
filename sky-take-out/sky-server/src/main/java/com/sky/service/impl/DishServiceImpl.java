package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.beans.beancontext.BeanContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class DishServiceImpl implements DishService {
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;


    @Override
    public PageResult page(DishPageQueryDTO pageDTO) {
        //设置分页参数
        PageHelper.startPage(pageDTO.getPage(), pageDTO.getPageSize(), false);
        //查询
        Page<DishVO> page = dishMapper.select(pageDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public void deleteBatch(List<Long> ids) {
        //获取要删除的菜品项
        ids.forEach(id -> {
            //查看为停售状态
            if(Objects.equals(dishMapper.selectById(id).getStatus(), StatusConstant.ENABLE))
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
        });
        //查看是否关联套餐
        ids.forEach(id -> {
            List<Long> mealIds= setmealDishMapper.getSetmealIdsByDishId(id);
            if(mealIds!=null&& !mealIds.isEmpty())
                throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        });

        //批量删除菜品
        dishMapper.deleteBatch(ids);
        //删除菜品口味
        dishFlavorMapper.deleteByDishId(ids);
    }

    @Override
    public List<DishVO> selectByCategoryId(Integer categoryId) {
        // 调用mapper查询菜品
        return dishMapper.selectByCategoryId(categoryId);
    }

    @Override
    public DishVO getDishById(Long id) {
        // 调用mapper查询菜品
        Dish dish = dishMapper.getDishById(id);
        // 返回菜品VO对象
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish, dishVO);
        // 为菜品VO对象设置菜品口味
        dishVO.setFlavors(dishFlavorMapper.getByDishId(id));
        // 返回菜品VO对象
        return dishVO;
    }

    @Override
    public void update(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        // 更新属性
        dishMapper.update(dish);
        //删除现有口味
        List<Long> ids = new ArrayList<>();
        ids.add(dish.getId());
        dishFlavorMapper.deleteByDishId(ids);
        //插入新的口味
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if(flavors!=null&&!flavors.isEmpty())
        {
            flavors.forEach(flavor -> flavor.setDishId(dish.getId()));
            dishFlavorMapper.insert(flavors);
        }
    }


    @Override
    public void startOrStop(Integer status, Long id) {
        // 调用mapper修改菜品状态
        dishMapper.update(Dish.builder().id(id).status(status).build());
    }

    @Override
    public void save(DishDTO dishdto) {
        // 拷贝属性,剩余口味flavors属性不拷贝
        Dish dish = Dish.builder().build();
        BeanUtils.copyProperties(dishdto, dish);
        // 保存菜品
        dishMapper.insert(dish);
        // 保存菜品口味flavors，List<DishFlavor>类型封装了口味信息
        List<DishFlavor> flavors = dishdto.getFlavors();

        // 遍历菜品口味集合，为每个菜品口味设置dish_id属性后插入到菜品口味表
        if(flavors!=null&& !flavors.isEmpty())
        {
            // 主键回显口味数组中的dish_id属性，为了后续插入菜品口味表使用
            flavors.forEach(flavor -> flavor.setDishId(dish.getId()));
            // 保存菜品口味
            dishFlavorMapper.insert(flavors);
        }
    }
}
