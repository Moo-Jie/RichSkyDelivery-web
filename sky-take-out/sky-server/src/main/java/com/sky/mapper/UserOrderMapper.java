package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.GoodsSalesDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface UserOrderMapper {
    /**
     *
     *
     * @param orders
     * @return void
     * @author DuRuiChi
     * @create 2024/11/2
     **/
    void insert(Orders orders);
    /**
     * 根据订单号和用户id查询订单
     * @param orderNumber
     * @param userId
     */

    @Select("select * from orders where number = #{orderNumber} and user_id= #{userId}")
    Orders getByNumberAndUserId(String orderNumber, Long userId);

    /**
     * 修改订单信息
     * @param orders
     */
    void update(Orders orders);

    @Update("update orders set status = #{orderStatus}, pay_status = #{orderPaidStatus}, checkout_time = #{chekOutTime} where number = #{orderNumber}")
    void updateStatus(Integer orderStatus, Integer orderPaidStatus, LocalDateTime chekOutTime, String orderNumber);

    Page<Orders> selecOrdertList(OrdersPageQueryDTO ordersPageQueryDTO);

    @Select("select * from orders where id = #{id}")
    Orders getOrderById(Long id);

    @Select("select count(*) from orders where status = #{status} ")
    Integer getOrderCountByStatus(Integer status);

    @Select("select * from orders where number = #{number}")
    Orders getByNumber(String number);

    @Select("select * from orders where status = 5")
    List<Orders> getOrderByStatusIsCompleted();

    @Select("select * from orders")
    List<Orders> getAllOrder();

    List<GoodsSalesDTO> getSalesTop10(LocalDateTime begin, LocalDateTime end);


    Integer countByMap(Map map);

    Double sumByMap(Map map);
}
