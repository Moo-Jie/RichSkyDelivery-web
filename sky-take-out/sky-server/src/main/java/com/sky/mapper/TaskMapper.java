package com.sky.mapper;

import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface TaskMapper {

    @Select("select * from orders where status = #{status} and order_time < #{orderTime}")
    List<Orders> getOrderByStatusAndOrderTime(Integer status, LocalDateTime orderTime);
}
