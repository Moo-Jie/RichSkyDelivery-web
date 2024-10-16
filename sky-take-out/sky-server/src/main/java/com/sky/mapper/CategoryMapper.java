package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.Annotation.AutoFileAssign;
import com.sky.entity.Category;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CategoryMapper {
    @AutoFileAssign(value = OperationType.INSERT)
    @Insert("insert into category (type, name, sort,status, create_time, update_time, create_user, update_user) "
            + "values (#{type}, #{name}, #{sort},#{status}, #{createTime}, #{updateTime}, #{createUser}, #{updateUser})")
    void save(Category category);

    Page<Category> selectByNameType(String name, Integer type);

    @Delete("delete from category where id = #{id}")
    void deleteById(Long id);
    @AutoFileAssign(value = OperationType.UPDATE)
    void update(Category category);

    @Select("select * from category where type = #{type}")
    List<Category> selectByType(Integer type);
}