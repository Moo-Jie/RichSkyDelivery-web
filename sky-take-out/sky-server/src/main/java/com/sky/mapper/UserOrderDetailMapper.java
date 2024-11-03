package com.sky.mapper;

import com.sky.entity.OrderDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserOrderDetailMapper {
    void insertBatch(List<OrderDetail> orderDetails);

    @Select("select * from order_detail where order_id = #{id}")
    List<OrderDetail> selectByOrderId(Long id);
}
