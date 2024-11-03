package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional()
@Service
@Slf4j
public class SetmealServiceImpl implements SetmealService {
    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;


    /**
     *  管理端查询功能
     * @param setmealPageQueryDTO
     * @return com.sky.result.PageResult
     * @author DuRuiChi
     * @create 2024/10/25
     **/

    @Override
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        //设置分页参数
        PageHelper.startPage(setmealPageQueryDTO.getPage(),setmealPageQueryDTO.getPageSize(),false);
        //执行查询
        Page<SetmealVO> page=setmealMapper.pageQuery(setmealPageQueryDTO);
        //封装结果
        return new PageResult(page.getTotal(),page.getResult());
    }

    /**
     *
     *
     * @param setmealDTO
     * @return void
     * @author DuRuiChi
     * @create 2024/10/25
     **/
    @CacheEvict(cacheNames = "SetmealCache",key = "#setmealDTO.categoryId")
    @Override
    public void save(SetmealDTO setmealDTO) {
        //Setmeal对象
        Setmeal setmeal=new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);
        //保存套餐表
        setmealMapper.save(setmeal);
        //设置套餐id
        List<SetmealDish> setmealDishes=setmealDTO.getSetmealDishes();
        for (SetmealDish setmealDish:setmealDishes){
            setmealDish.setSetmealId(setmeal.getId());
        }
        //保存套餐和菜品的关联关系
        setmealDishMapper.save(setmealDishes);

        log.info("套餐信息保存成功,清空缓存" );
    }

    /**
     *
     *
     * @param id
     * @return com.sky.vo.SetmealVO
     * @author DuRuiChi
     * @create 2024/10/25
     **/
    @Override
    public SetmealVO getById(Long id) {
        //根据id查询套餐
        Setmeal setmeal=setmealMapper.getById(id);
        //封装为SetmealVO
        SetmealVO setmealVO=new SetmealVO();
        BeanUtils.copyProperties(setmeal,setmealVO);
        //根据套餐id查询菜品
        setmealVO.setSetmealDishes(setmealDishMapper.getBySetmealId(id));
        return setmealVO;
    }

    /**
     *
     *
     * @param setmealDTO
     * @return void
     * @author DuRuiChi
     * @create 2024/10/25
     **/
    @CacheEvict(cacheNames = "SetmealCache",key = "#setmealDTO.categoryId")
    @Override
    public void update(SetmealDTO setmealDTO) {
        //更新
        Setmeal setmeal=new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);
        setmealMapper.update(setmeal);
        //更新套餐菜品关系表中的数据
        setmealDishMapper.deleteBySetmealId(setmeal.getId());
        //重新保存套餐和菜品的关联关系
        List<SetmealDish> dishes = setmealDTO.getSetmealDishes();
        dishes.forEach(dish -> dish.setSetmealId(setmeal.getId()));
        setmealDishMapper.save(dishes);

        log.info("套餐信息修改成功,清空缓存" );
    }

    /**
     *
     *
     * @param status
     * @param id
     * @return void
     * @author DuRuiChi
     * @create 2024/10/25
     **/
    @CacheEvict(cacheNames = "SetmealCache",allEntries = true)//因为不确定是哪个分类，因此要删除所有的缓存数据
    @Override
    public void startOrStop(Integer status, Integer id) {
        setmealMapper.startOrStop(status,id);

        log.info("套餐状态修改成功,清空缓存" );
    }

    /**
     * @param categoryId
     * @return
     */
    @Cacheable(cacheNames = "SetmealCache",key = "#categoryId")//根据分类id缓存数据
    @Override
    public List<SetmealVO> listBycategoryId(Integer categoryId) {
        List<SetmealVO> setmealVOList = setmealMapper.listBycategoryId(categoryId);
        log.info("套餐信息查询成功,已缓存数据至Redis" );
        return setmealVOList;
    }

    /**
     *
     * @param id
     * @return
     */
    @Override
    public List<DishItemVO> getDishBySetmealId(Integer id) {
        //根据套餐id查询菜品
        List<DishItemVO> dishs = setmealDishMapper.getDishItemBySetmealId(id);
        //返回菜品
        return dishs;
    }

    /**
     *
     *
 * @param ids
     * @return void
     * @author DuRuiChi
     * @create 2024/10/25
     **/
    @CacheEvict(cacheNames = "SetmealCache",allEntries = true)//因为不确定是哪个分类，因此要删除所有的缓存数据
    @Override
    public void deleteByIds(List<Long> ids) {
        ids.forEach(id -> {
            if(StatusConstant.ENABLE == setmealMapper.getById(id).getStatus()){
                //起售中的套餐不能删除
                throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
            }
        });

        ids.forEach(setmealId -> {
            //删除套餐表中的数据
            setmealMapper.deleteById(setmealId);
            //删除套餐菜品关系表中的数据
            setmealDishMapper.deleteBySetmealId(setmealId);
        });

        log.info("套餐信息删除成功,清空缓存" );
    }
}
