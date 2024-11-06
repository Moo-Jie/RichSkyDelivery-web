package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.Annotation.AutoFileAssign;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.enumeration.OperationType;
import com.sky.vo.SetmealVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

@Mapper
public interface SetmealMapper {
    /**
     * 根据条件统计套餐数量
     * @param map
     * @return
     */
    Integer countByMap(Map map);
    /**
     * 根据分类id查询套餐的数量
     * @param id
     * @return
     */
    @Select("select count(id) from setmeal where category_id = #{categoryId}")
    Integer countByCategoryId(Long id);

    Page<SetmealVO> pageQuery(SetmealPageQueryDTO setmeal);

    @AutoFileAssign(value = OperationType.INSERT)
    void save(Setmeal setmeal);

    @Select("select * from setmeal where id = #{id}")
    Setmeal getById(Long id);

    @Delete("delete from setmeal where id = #{setmealId}")
    void deleteById(Long setmealId);

    @AutoFileAssign(value = OperationType.UPDATE)
    void update(Setmeal setmeal);

    @Update("update setmeal set status = #{status} where id = #{id}")
    void startOrStop(Integer status, Integer id);

    @Select("select * from setmeal where category_id = #{categoryId}")
    List<SetmealVO> listBycategoryId(Integer categoryId);

}
