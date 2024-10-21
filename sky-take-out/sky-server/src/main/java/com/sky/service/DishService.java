package com.sky.service;


import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

public interface DishService {
    void save(DishDTO dish);

    PageResult page(DishPageQueryDTO pageDTO);

    void deleteBatch(List<Long> ids);

    List<DishVO> selectByCategoryId(Integer categoryId);

    DishVO getDishById(Long id);

    void update(DishDTO dishDTO);

    void startOrStop(Integer status, Long id);
}
