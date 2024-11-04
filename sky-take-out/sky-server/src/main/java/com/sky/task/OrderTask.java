package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.TaskMapper;
import com.sky.mapper.UserOrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Transactional
@Component
@Slf4j
//Scheduled 安排，计划
public class OrderTask {
    @Autowired
    private TaskMapper taskMapper;
    @Autowired
    private UserOrderMapper userOrderMapper;

    /**
     * 定时处理支付超时订单
     *
     * @return void
     * @author DuRuiChi
     * @create 2024/11/4
     **/
    @Scheduled(cron = "0 * * * * ?")//每分钟执行一次
    public void taskProcessPayTimeoutOrder(){
        log.info("定时处理支付超时订单为已取消");
        //查获所有未支付且超时订单
        List<Orders> TimeoutOrders = taskMapper.getOrderByStatusAndOrderTime
                (Orders.PENDING_PAYMENT, LocalDateTime.now().plusMinutes(-15));
        //处理超时订单
        if(TimeoutOrders!=null && !TimeoutOrders.isEmpty()){
            for (Orders orders : TimeoutOrders) {
                orders.setStatus(Orders.CANCELLED);
                orders.setCancelReason("支付超时，自动取消");
                orders.setCancelTime(LocalDateTime.now());
                userOrderMapper.update(orders);
            }
        }
    }

    /**
     * 定时处理派送超时订单
     *
     * @return void
     * @author DuRuiChi
     * @create 2024/11/4
     **/
    @Scheduled(cron = "0 0 1 * * ?")//每天凌晨一点执行
    public void taskProcessDeliveryTimeoutOrder(){
        log.info("定时处理派送超时订单为已完成");
        //查找所有派送中且在凌晨一点订单
        List<Orders> TimeoutOrders = taskMapper.getOrderByStatusAndOrderTime
                (Orders.DELIVERY_IN_PROGRESS, LocalDateTime.now().plusMinutes(-60));
        //处理超时订单
        if(TimeoutOrders!=null &&!TimeoutOrders.isEmpty()) {
            for (Orders orders : TimeoutOrders) {
                orders.setStatus(Orders.COMPLETED);
                orders.setDeliveryTime(LocalDateTime.now());
                userOrderMapper.update(orders);
            }
        }
    }
}
