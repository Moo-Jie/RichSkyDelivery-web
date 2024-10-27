package com.sky.mapper;

import com.sky.Annotation.AutoFileAssign;
import com.sky.entity.Setmeal;
import com.sky.entity.User;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {
    @Select("select * from user where openid = #{openid}")
    User getByOpenid(String openid);

    void insert(User build);

    @Select("select * from setmeal where category_id = #{categoryId}")
    Setmeal listBycategoryId(Integer categoryId);
}
