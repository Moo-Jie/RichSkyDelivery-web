package com.sky.controller.user;

import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/user/order")
@Api(tags = "C端用户订单接口")
public class UserOrderContorller {
    @Autowired
    private OrderService orderService;

    /**
     * 用户下单接口实现
     *
 * @param ordersSubmitDTO
     * @return com.sky.result.Result<com.sky.vo.OrderSubmitVO>
     * @author DuRuiChi
     * @create 2024/11/1
     **/
    @PostMapping("/submit")
    @ApiOperation("用户下单")
    public Result<OrderSubmitVO> submit(@RequestBody OrdersSubmitDTO ordersSubmitDTO) {
        log.info("用户下单：{}", ordersSubmitDTO);
        return Result.success(orderService.submitOrder(ordersSubmitDTO));
    }
    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    @PutMapping("/payment")
    @ApiOperation("订单支付")
    public Result<OrderPaymentVO> payment(@RequestBody OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        log.info("订单支付：{}", ordersPaymentDTO);
        OrderPaymentVO orderPaymentVO = orderService.payment(ordersPaymentDTO);
        log.info("生成预支付交易单：{}", orderPaymentVO);
        return Result.success(orderPaymentVO);
    }

    /**
     * 历史订单查询 
     * 
 * @param page
 * @param pageSize
 * @param status
     * @return com.sky.result.Result<com.sky.result.PageResult>
     * @author DuRuiChi
     * @create 2024/11/3
     **/
    @GetMapping("/historyOrders")
    @ApiOperation("历史订单查询")
    public Result<PageResult> historyOrders(int page, int pageSize,Integer status) {//status 允许为空，因此用包装类
        log.info("历史订单查询：{},{},{}", page, pageSize,status);
        return Result.success(orderService.historyOrders(page, pageSize,status));
    }

    /**
     * 查询订单详情
     *
     * @param id
     * @return
     */
    @GetMapping("/orderDetail/{id}")
    @ApiOperation("查询订单详情")
    public Result<OrderVO> orderDetail(@PathVariable Long id) {
        log.info("查询订单详情：{}", id);
        OrderVO orderVO = orderService.SelectOrderAndOrderDetailById(id);
        return Result.success(orderVO);
    }

    @PutMapping("/cancel/{id}")
    @ApiOperation("用户取消订单")
    public Result cancel(@PathVariable Long id) {
        log.info("用户取消订单：{}", id);
        orderService.UserCancel(id);
        return Result.success();
    }

    @PostMapping("/repetition/{id}")
    @ApiOperation("用户再次下单")
    public Result repetition(@PathVariable Long id) {
        log.info("用户再次下单：{}", id);
        orderService.repetition(id);
        return Result.success();
    }
}
