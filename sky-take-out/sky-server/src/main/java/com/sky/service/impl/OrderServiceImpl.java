package com.sky.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.*;
import com.sky.entity.AddressBook;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import com.sky.entity.ShoppingCart;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.OrderBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.*;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.utils.WeChatPayUtil;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PutMapping;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Transactional
@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private UserOrderMapper userOrderMapper;
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private UserOrderDetailMapper userOrderDetailMapper;
    @Autowired
    private AddressBookMapper addressBookMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private WeChatPayUtil weChatPayUtil;
    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        // 当前登录用户id
        //Long userId = BaseContext.getCurrentId();
        //User user = userMapper.getById(userId);

        //调用微信支付接口，生成预支付交易单
//        JSONObject jsonObject = weChatPayUtil.pay(
//                ordersPaymentDTO.getOrderNumber(), //商户订单号
//                new BigDecimal(0.01), //支付金额，单位 元
//                "苍穹外卖订单", //商品描述
//                user.getOpenid() //微信用户的openid
//        );
//
//        if (jsonObject.getString("code") != null && jsonObject.getString("code").equals("ORDERPAID")) {
//            throw new OrderBusinessException("该订单已支付");
//        }

        //直接封装假的支付返回结果

        //模拟预支付订单，设置状态为：订单已支付
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", "ORDERPAID");
        OrderPaymentVO vo = jsonObject.toJavaObject(OrderPaymentVO.class);
        //模拟下单接口返回的 prepay_id 参数值
        vo.setPackageStr(jsonObject.getString("package"));
        //模拟订单状态
        Integer OrderPaidStatus = Orders.PAID;//已支付
        Integer OrderStatus = Orders.TO_BE_CONFIRMED;//未支付
        //模拟支付时间，直接返回当前时间
        LocalDateTime chek_out_time = LocalDateTime.now();
        //添加订单号
        String orderNumber = ordersPaymentDTO.getOrderNumber();
        //把模拟的已完成的订单同步到数据库
        userOrderMapper.updateStatus(OrderStatus,OrderPaidStatus,chek_out_time,orderNumber);
        return vo;
    }

    /**
     * 支付成功，修改订单状态
     *
     * @param outTradeNo
     */
    public void paySuccess(String outTradeNo) {
        // 当前登录用户id
        Long userId = BaseContext.getCurrentId();

        // 根据订单号查询当前用户的订单
        Orders ordersDB = userOrderMapper.getByNumberAndUserId(outTradeNo, userId);

        // 根据订单id更新订单的状态、支付方式、支付状态、结账时间
        Orders orders = Orders.builder()
                .id(ordersDB.getId())
                .status(Orders.TO_BE_CONFIRMED)
                .payStatus(Orders.PAID)
                .checkoutTime(LocalDateTime.now())
                .build();

        userOrderMapper.update(orders);
    }

    /**
     * @param page
     * @param pageSize
     * @param status
     * @return
     */
    @Override
    public PageResult historyOrders(int page, int pageSize, Integer status) {
        //设置分页参数
        PageHelper.startPage(page, pageSize);
        //条件查询并自动分页
        OrdersPageQueryDTO ordersPageQueryDTO = new OrdersPageQueryDTO();
        ordersPageQueryDTO.setStatus(status);
        ordersPageQueryDTO.setUserId(BaseContext.getCurrentId());
        Page<Orders> pages = userOrderMapper.selecOrdertList(ordersPageQueryDTO);
        //封装VO，添加订单明细
        List<OrderVO> ordersVoList =new ArrayList<>();
        if(pages != null || pages.getTotal() > 0)
        {
            pages.forEach(orderPage -> {
                OrderVO orderVO = new OrderVO();
                // 拷贝订单属性
                BeanUtils.copyProperties(orderPage, orderVO);//OrderVO继承了Orders
                //拷贝订单明细属性
                orderVO.setOrderDetailList(userOrderDetailMapper.selectByOrderId(orderPage.getId()));
                ordersVoList.add(orderVO);
            });
        }
        //返回
        return  new PageResult(pages.getTotal(),ordersVoList);
    }

    /**
     * @param id
     * @return
     */
    @Override
    public OrderVO SelectOrderAndOrderDetailById(Long id) {
       OrderVO orderVO = new OrderVO();
        Orders orders = userOrderMapper.getOrderById(id);
        BeanUtils.copyProperties(orders,orderVO);
       orderVO.setOrderDetailList(userOrderDetailMapper.selectByOrderId(orders.getId()));
       return orderVO;
    }

    /**
     * @param userId
     */
    @Override
    public void UserCancel(Long userId) {
        //1.查看订单是否存在
        Orders order = userOrderMapper.getOrderById(userId);
        if(order == null)
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        //2.不予取消的订单状态 （1待付款 2待接单 3已接单 4派送中 5已完成 6已取消）
        if(order.getStatus() > 2)
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        //3.待接单要退款
        if(order.getStatus().equals(Orders.TO_BE_CONFIRMED))
        {
            //退款
            log.info("ID为{}的用户已退款, 退款金额为{}",order.getId(),order.getAmount());
            //设置支付状态为已退款
            order.setPayStatus(Orders.REFUND);
        }
        //4.设置订单状态、取消原因、取消时间
        order.setStatus(Orders.CANCELLED);
        order.setCancelReason("用户已取消该订单");
        order.setCancelTime(LocalDateTime.now());
        //5.更新订单状态
        userOrderMapper.update(order);
    }

    /**
     * @param id
     */
    @Override
    public void repetition(Long id) {
        //1.订单明细查询
        List<OrderDetail> orderDetails = userOrderDetailMapper.selectByOrderId(id);
        //2.由于添加订单明细时是从购物车录入信息的，因此直接拿回来即可
        List<ShoppingCart> shoppingCarts = new ArrayList<>();
        Long userID = BaseContext.getCurrentId();
        orderDetails.forEach(orderDetail -> {
            ShoppingCart shoppingCart = ShoppingCart.builder()
                    .userId(userID)
                    .build();
            BeanUtils.copyProperties(orderDetail, shoppingCart,"id");
            shoppingCarts.add(shoppingCart);
        });
        //3.将购物车数据插入到数据库
        shoppingCartMapper.insertBatch(shoppingCarts);
    }

    /**
     * @param ordersPageQueryDTO
     * @return
     */
    @Override
    public PageResult conditionSelect(OrdersPageQueryDTO ordersPageQueryDTO) {
        //设置分页参数
        PageHelper.startPage(ordersPageQueryDTO.getPage(), ordersPageQueryDTO.getPageSize());
        //条件查询并自动分页
        Page<Orders> pages = userOrderMapper.selecOrdertList(ordersPageQueryDTO);
        //返回
        return  new PageResult(pages.getTotal(),toOrderVOList(pages));
    }

    /**
     * @return
     */
    @Override
    public OrderStatisticsVO statistics() {
        //1.查询待接单订单数量
        Integer toBeConfirmed = userOrderMapper.getOrderCountByStatus(Orders.TO_BE_CONFIRMED);
        //2.查询待派送数量
        Integer confirmed = userOrderMapper.getOrderCountByStatus(Orders.CONFIRMED);
        //3.查询派送中数量
        Integer deliveryInProgress = userOrderMapper.getOrderCountByStatus(Orders.DELIVERY_IN_PROGRESS);

        //4.封装返回结果
        return OrderStatisticsVO.builder()
             .toBeConfirmed(toBeConfirmed)//待接单数量
             .confirmed(confirmed)//待派送数量
             .deliveryInProgress(deliveryInProgress)//派送中数量
             .build();
    }

    /**
     *  根据orderID查询订单信息及详细信息
     * 
 * @param id
     * @return com.sky.vo.OrderVO
     * @author DuRuiChi
     * @create 2024/11/3
     **/
    @Override
    public OrderVO details(Long id) {
        OrderVO orderVO = new OrderVO();
        Orders orders = userOrderMapper.getOrderById(id);
        BeanUtils.copyProperties(orders,orderVO);
        orderVO.setOrderDetailList(userOrderDetailMapper.selectByOrderId(orderVO.getId()));
        return orderVO;
    }

    /**
     * 接单
     *
     * @param ordersConfirmDTO
     */
    public void confirm(OrdersConfirmDTO ordersConfirmDTO) {
        userOrderMapper.update(Orders.builder()
                .id(ordersConfirmDTO.getId())
                .status(Orders.CONFIRMED)
                .build());
    }

    /**
     * @param ordersRejectionDTO
     */
    @Override
    public void rejection(OrdersRejectionDTO ordersRejectionDTO) {
        //1.查询订单信息
        Orders orders = userOrderMapper.getOrderById(ordersRejectionDTO.getId());
        //2.判断订单是否存在且为待接单
        if(orders == null ||!orders.getStatus().equals(Orders.TO_BE_CONFIRMED))
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        //3.退款
        log.info("ID为{}的用户由于商家拒单已退款, 退款金额为{}",orders.getId(),orders.getAmount());
        //设置支付状态为已退款
        orders.setPayStatus(Orders.REFUND);
        //4.设置订单状态、拒单原因、取消时间
        orders.setStatus(Orders.CANCELLED);
        orders.setRejectionReason(ordersRejectionDTO.getRejectionReason());
        orders.setCancelTime(LocalDateTime.now());
        //5.更新订单状态
        userOrderMapper.update(orders);
    }

    /**
     * @param ordersCancelDTO
     */
    @Override
    public void adminCancel(OrdersCancelDTO ordersCancelDTO) {
        //1.查询订单信息
        Orders orders = userOrderMapper.getOrderById(ordersCancelDTO.getId());
        //2.若用户已经支付，则退款
        if(orders.getPayStatus().equals(Orders.PAID))
        {
            log.info("ID为{}的用户由于管理端取消订单已退款, 退款金额为{}",orders.getId(),orders.getAmount());
            //设置支付状态为已退款
            orders.setPayStatus(Orders.REFUND);
        }
        //3.设置订单状态、取消原因、取消时间
        orders.setStatus(Orders.CANCELLED);
        orders.setCancelReason(ordersCancelDTO.getCancelReason());
        orders.setCancelTime(LocalDateTime.now());
        //4.更新订单状态
        userOrderMapper.update(orders);
    }

    /**
     * @param id
     */
    @Override
    public void delivery(Long id) {
        //1.查询订单信息
        Orders orders = userOrderMapper.getOrderById(id);
        //2.待派送的订单才能参与派送
        if(orders ==null ||!orders.getStatus().equals(Orders.CONFIRMED))
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        //3.设置订单状态为派送中
        orders.setStatus(Orders.DELIVERY_IN_PROGRESS);
        //4.更新订单状态
        userOrderMapper.update(orders);
    }

    /**
     * @param id
     */
    @Override
    public void complete(Long id) {
        //1.查询订单信息
        Orders orders = userOrderMapper.getOrderById(id);
        //2.派送中的订单才能被设置送达
        if(orders ==null ||!orders.getStatus().equals(Orders.DELIVERY_IN_PROGRESS))
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        //3.设置订单状态为已完成、送达时间
        orders.setStatus(Orders.COMPLETED);
        orders.setDeliveryTime(LocalDateTime.now());
        //4.更新订单状态
        userOrderMapper.update(orders);

    }

    /**
     * Page<Orders>转换为List<OrderVO>
     *
 * @param pages
     * @return java.util.List<com.sky.vo.OrderVO>
     * @author DuRuiChi
     * @create 2024/11/3
     **/
    private List<OrderVO> toOrderVOList(Page<Orders> pages) {
        List<OrderVO> orderVOList = new ArrayList<>();
        pages.forEach(order -> {
            OrderVO orderVO = new OrderVO();
            BeanUtils.copyProperties(order,orderVO);
            //设置菜品简介
            orderVO.setOrderDishes(getDishesStrByOrderId(orderVO.getId()));
            orderVOList.add(orderVO);
        });
        return orderVOList;
    }


    /**
     *  生成字符串OrderVO::OrderDishes，用于展示菜品简介
     *
 * @param orderId
     * @return java.lang.String
     * @author DuRuiChi
     * @create 2024/11/3
     **/
    private String getDishesStrByOrderId(Long orderId) {
        List<OrderDetail> orderDetails = userOrderDetailMapper.selectByOrderId(orderId);
        List<String> OrderDishes = orderDetails.stream()
                .map(orderDetail -> orderDetail.getName() + "*" + orderDetail.getNumber())
                .collect(Collectors.toList());
        return String.join("",OrderDishes);
    }


    /**
     *  下单接口实现
     *
 * @param ordersSubmitDTO
     * @return void
     * @author DuRuiChi
     * @create 2024/11/1
     **/
    @Override
    public OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO) {
        //1.校验数据合法性
        //校验订单的地址是否存在
        AddressBook address = addressBookMapper.getById(ordersSubmitDTO.getAddressBookId());
        if(address == null)
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        //校验当前用户的购物车数据是否为空
        List<ShoppingCart> shoppingCarts = shoppingCartMapper.selectList(ShoppingCart.builder().userId(BaseContext.getCurrentId()).build());
        if(shoppingCarts == null || shoppingCarts.isEmpty())
            throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);

        //2.构造订单数据
        Orders order = new Orders();
        //将ordersSubmitDTO中的属性拷贝到order中
        BeanUtils.copyProperties(ordersSubmitDTO,order);
        //其他属性
        order.setPhone(address.getPhone());//手机号
        order.setAddress(address.getDetail());//详细地址
        order.setConsignee(address.getConsignee());//收货人
        order.setNumber(String.valueOf(System.currentTimeMillis()));//订单号是通过将当前时间的毫秒值转换为字符串来生成的
        order.setUserId(BaseContext.getCurrentId());
        order.setStatus(Orders.PENDING_PAYMENT);//待付款
        order.setPayStatus(Orders.UN_PAID);//未支付
        order.setOrderTime(LocalDateTime.now());//订单时间

        //3.向订单表插入一条订单数据
        userOrderMapper.insert(order);

        //4.向订单明细表批量插入多条数据
        // （订单明细表实质上就是订单表和购物车表的中间表，一个订单对应购物车内的多种菜品信息）
        List<OrderDetail> orderDetails = new ArrayList<>();
        //购物车信息,依次插入菜品或套餐到订单明细表中
        shoppingCarts.forEach(shoppingCart -> {
            OrderDetail orderDetail=new OrderDetail();
            BeanUtils.copyProperties(shoppingCart,orderDetail);
            orderDetail.setOrderId(order.getId());
            orderDetails.add(orderDetail);
        });
        userOrderDetailMapper.insertBatch(orderDetails);

        //5.清空购物车数据
        shoppingCartMapper.deleteByUserId(ShoppingCart.builder().userId(BaseContext.getCurrentId()).build());

        //6.封装返回结果
        return OrderSubmitVO.builder()
                .id(order.getId())
                .orderNumber(order.getNumber())
                .orderAmount(order.getAmount())
                .orderTime(order.getOrderTime())
                .build();
    }
}
