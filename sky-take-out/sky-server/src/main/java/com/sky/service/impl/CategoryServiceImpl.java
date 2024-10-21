package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.CategoryMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.CategoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private DishMapper dishMapper;

    @Override
    public void save(CategoryDTO categoryDTO) {
        Category category = new Category();
        //将DTO中的属性拷贝到category中
        BeanUtils.copyProperties(categoryDTO, category);
        //设置其他属性
        category.setStatus(StatusConstant.ENABLE);
//        category.setCreateTime(LocalDateTime.now());AOP内实现
//        category.setUpdateTime(LocalDateTime.now());AOP内实现
//        category.setCreateUser(BaseContext.getCurrentId());AOP内实现
//        category.setUpdateUser(BaseContext.getCurrentId());AOP内实现
        //保存到数据库
        categoryMapper.save(category);
    }

    @Override
    public PageResult pageQuery(CategoryPageQueryDTO categoryPageQueryDTO) {
        //设置分页参数
        PageHelper.startPage(categoryPageQueryDTO.getPage(), categoryPageQueryDTO.getPageSize());

        //查询数据
        Page<Category> page=categoryMapper.selectByNameType(categoryPageQueryDTO.getName(), categoryPageQueryDTO.getType());

        //封装结果
        return new PageResult(page.getTotal(), page.getResult());
        //设置分页参数

    }

    @Override
    public void deleteById(Long id) {
        //查询当前分类是否关联了菜品，如果关联了，抛出业务异常
        if(dishMapper.countByCategoryId(id)>0)
            throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_DISH);
        //查询当前分类是否关联了套餐，如果关联了，抛出业务异常
        if(setmealMapper.countByCategoryId(id)>0)
            throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_SETMEAL);
        //删除分类
        categoryMapper.deleteById(id);
    }

    @Override
    public void update(CategoryDTO categoryDTO) {
        //将DTO中的属性拷贝到category中
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO, category);
        //设置其他属性
        //category.setUpdateTime(LocalDateTime.now());AOP内实现
        //category.setUpdateUser(BaseContext.getCurrentId());AOP内实现
        //保存到数据库
        categoryMapper.update(category);
    }

    @Override
    public void startOrStop(Integer status, Long id) {
        Category category = Category.builder()
               .status(status)
               .id(id)
               .build();
        categoryMapper.update(category);
    }

    @Override
    public List<Category> list(Integer type) {
        List<Category> list = categoryMapper.selectByType(type);
        return list;
    }
}
