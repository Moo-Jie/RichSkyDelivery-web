package com.sky.mapper;

import com.sky.entity.Setmeal;
import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {
    @Select("select * from user where openid = #{openid}")
    User getByOpenid(String openid);

    void insert(User build);

    @Select("select * from setmeal where category_id = #{categoryId}")
    Setmeal listBycategoryId(Integer categoryId);

    @Select("select * from user where id = #{userId}")
    User getById(Long userId);
}
