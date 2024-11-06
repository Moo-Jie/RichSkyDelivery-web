package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.entity.User;
import com.sky.mapper.UserMapper;
import com.sky.mapper.UserOrderMapper;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Transactional
@Service
public class ReportServiceImpl implements ReportService {
    @Autowired
    private UserOrderMapper userOrderMapper;
    @Autowired
    private UserMapper userMapper;

    /**
     * @param begin
     * @param end
     * @return
     */
    @Override
    public OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end) {
        //1.生成日期
        List<LocalDate> dateList=new ArrayList<>();
        while(!begin.equals(end))
        {
            begin=begin.plusDays(1);
            dateList.add(begin);
        }
        dateList.add(end.plusDays(1));
        //2.初始化各项指标
        Integer totalOrderCount=0;//总订单数量
        Integer validOrderCount=0;//有效订单数量
        List<Orders> orders=userOrderMapper.getAllOrder(); //获取所有订单
        List<Integer> orderCountList=new ArrayList<>();//生成订单数量
        List<Integer> validOrderCountList=new ArrayList<>();//生成有效订单数量
        Double orderCompletionRate = 0.0;

        //3.遍历日期集合，计算报表数据
        for (LocalDate date : dateList) {
            //每日订单数量和有效订单数量
            Integer todayOrderCount=0;//当日订单数量
            Integer todayValidOrderCount=0;//当日有效订单数量

            //为date当日设置开始和结束时间
            //把LocalDate和LocalTime合并成LocalDateTime
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            //遍历所有订单，判断订单时间是否在当日
            for (Orders order : orders) {
                if (order.getOrderTime().isAfter(beginTime) && order.getOrderTime().isBefore(endTime)) {
                    //当日有效订单数量加1
                    if (order.getStatus() == 5)
                        todayValidOrderCount++;
                    //当日订单数量加1
                    todayOrderCount++;
                }
            }
            //添加到订单数量和有效订单数量集合中
            orderCountList.add(todayOrderCount);
            validOrderCountList.add(todayValidOrderCount);
            //总订单数量
            totalOrderCount+=todayOrderCount;
            validOrderCount+=todayValidOrderCount;
        }
        //订单完成率
        if(totalOrderCount != 0){
            orderCompletionRate = validOrderCount.doubleValue() / totalOrderCount;
        }


        //4.VO数据封装
        return OrderReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .orderCountList(StringUtils.join(orderCountList, ","))
                .validOrderCountList(StringUtils.join(validOrderCountList, ","))
                .totalOrderCount(totalOrderCount)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .build();
    }

    /**
     * 本月至今为止的10个销售量最高的菜品
     * 
 * @param begin
 * @param end
     * @return com.sky.vo.SalesTop10ReportVO
     * @author DuRuiChi
     * @create 2024/11/6
     **/
    @Override
    public SalesTop10ReportVO getSalesTop10(LocalDate begin, LocalDate end) {
        //1.获取所有位于日期内的,状态为已完成的,销售量前10订单
        List<GoodsSalesDTO> top10 = userOrderMapper.getSalesTop10
                (LocalDateTime.of(begin, LocalTime.MIN)
                , LocalDateTime.now());//LocalDateTime.of(end, LocalTime.MAX));
        //3.VO数据封装
        return SalesTop10ReportVO.builder()
                .nameList(StringUtils.join(
                        top10.stream()
                                .map(GoodsSalesDTO::getName)
                                .collect(Collectors.toList()), ","))
                .numberList(StringUtils.join(
                        top10.stream()
                                .map(GoodsSalesDTO::getNumber)
                                .collect(Collectors.toList()),","))
                .build();
    }

    /**
     * @param begin
     * @param end
     * @return
     */

    @Override
    public UserReportVO userStatistics(LocalDate begin, LocalDate end) {
        //1.生成日期
        List<LocalDate> dateList=new ArrayList<>();
        while(!begin.equals(end))
        {
            begin=begin.plusDays(1);
            dateList.add(begin);
        }
        dateList.add(end.plusDays(1));

        //2.生成总员工数量和新增员工数量
        List<User> users = userMapper.getAllOrder();
        List<Integer> totalUserList=new ArrayList<>();
        List<Integer> newUserList=new ArrayList<>();

        //遍历日期集合，计算总员工数量和新增员工数量
        Integer totalUser=0;//总员工数量
        for (LocalDate date : dateList) {
            //为date这一天设置开始和结束时间
            //把LocalDate和LocalTime合并成LocalDateTime
            LocalDateTime beginTime=LocalDateTime.of(date,LocalTime.MIN );
            LocalDateTime endTime=LocalDateTime.of(date, LocalTime.MAX);
            Integer newUser=0;//新增员工数量
            //遍历所有订单，判断订单时间是否在这一天之内
            for (User user : users) {
                //用户创建日期是否在这一天之内
                if(user.getCreateTime().isAfter(beginTime)&&user.getCreateTime().isBefore(endTime))
                {
                    //总员工数量加1
                    totalUser++;
                    //新用户数量加1
                    newUser++;
                }
            }
            //添加到当日总员工数量和新增员工数量集合中
            totalUserList.add(totalUser);
            newUserList.add(newUser);
        }

        //3.VO数据封装
        return UserReportVO.builder()
                .dateList(StringUtils.join(dateList,","))
                .newUserList(StringUtils.join(newUserList,","))
                .totalUserList(StringUtils.join(totalUserList,","))
                .build();
}

    /**
     * @param begin
     * @param end
     * @return
     */
    @Override
    public TurnoverReportVO turnoverStatistics(LocalDate begin, LocalDate end) {
        //1.生成日期
        List<LocalDate> dateList=new ArrayList<>();
        while(!begin.equals(end))
        {
            begin=begin.plusDays(1);
            dateList.add(begin);
        }
        dateList.add(end.plusDays(1));
        //2.生成营业额
        List<Double> turnoverList=new ArrayList<>();
        //获取所有订单状态为已完成的订单
        List<Orders> ordersList=userOrderMapper.getOrderByStatusIsCompleted();
        dateList.forEach(date->{

            //为date这一天设置开始和结束时间
            //把LocalDate和LocalTime合并成LocalDateTime
            LocalDateTime beginTime=LocalDateTime.of(date,LocalTime.MIN );
            LocalDateTime endTime=LocalDateTime.of(date, LocalTime.MAX);

            //遍历所有订单，判断订单时间是否在这一天之内
            Double turnover=0.0;
            for (Orders orders : ordersList) {
                if(orders.getOrderTime().isAfter(beginTime)&&orders.getOrderTime().isBefore(endTime))
                {
                    //营业额求和
                    if(orders.getAmount()!=null)
                        turnover+=orders.getAmount().doubleValue();
                }
            }

            //添加到本天营业额集合中
            turnoverList.add(turnover);
        });

        //3.VO数据封装
        return TurnoverReportVO.builder()
                .dateList(StringUtils.join(dateList,","))
                .turnoverList(StringUtils.join(turnoverList,","))
                .build();
    }
}
