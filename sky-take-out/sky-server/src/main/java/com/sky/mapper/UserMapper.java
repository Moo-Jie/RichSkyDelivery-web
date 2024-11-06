package com.sky.mapper;

import com.sky.entity.Orders;
import com.sky.entity.Setmeal;
import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserMapper {
    @Select("select * from user where openid = #{openid}")
    User getByOpenid(String openid);

    void insert(User build);

    @Select("select * from setmeal where category_id = #{categoryId}")
    Setmeal listBycategoryId(Integer categoryId);

    @Select("select * from user where id = #{userId}")
    User getById(Long userId);

    @Select("select * from user")
    List<User> getAllOrder();

    Integer countByMap(Map map);
}
