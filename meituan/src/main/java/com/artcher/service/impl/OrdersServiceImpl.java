package com.artcher.service.impl;

import com.artcher.common.BaseContext;
import com.artcher.domain.AddressBook;
import com.artcher.domain.OrderDetail;
import com.artcher.domain.Orders;
import com.artcher.domain.ShoppingCart;
import com.artcher.domain.User;
import com.artcher.dto.OrdersDto;
import com.artcher.exception.CustomException;
import com.artcher.mapper.OrdersMapper;
import com.artcher.service.AddressBookService;
import com.artcher.service.OrderDetailService;
import com.artcher.service.OrdersService;
import com.artcher.service.ShoppingCartService;
import com.artcher.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {
    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private UserService userService;

    @Autowired
    private AddressBookService addressBookService;

    @Autowired
    private OrderDetailService orderDetailService;


    /**
     * 保存订单信息和订单详情 addID,payMethod,remork
     * @param orders
     */
    @Override
    public void saveWithDetail(Orders orders) {
        OrdersDto ordersDto = new OrdersDto();
        //获取登陆用户的userId
        Long userId = BaseContext.getCurrentId();

        //通过userId查询用户购物车信息
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,userId);
        //购物车清单
        List<ShoppingCart> list = shoppingCartService.list(queryWrapper);
        if (list == null && list.size() ==0){
            throw new CustomException("无购物车信息");
        }

        //查询用户信息
        User user = userService.getById(userId);

        //查询地址数据
        AddressBook addressBook = addressBookService.getById(orders.getAddressBookId());
        if (addressBook == null){
            throw new CustomException("用户地址异常");
        }

        //给订单号赋值
        Long orderId = IdWorker.getId();

        AtomicInteger amount = new AtomicInteger(0);

        List<OrderDetail> orderDetails = list.stream().map(item -> {
            //遍历购物车list,计算总金额
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderId);
            orderDetail.setNumber(item.getNumber());
            orderDetail.setDishFlavor(item.getDishFlavor());
            orderDetail.setDishId(item.getDishId());
            orderDetail.setSetmealId(item.getSetmealId());
            orderDetail.setName(item.getName());
            orderDetail.setImage(item.getImage());
            orderDetail.setAmount(item.getAmount());
            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
            return orderDetail;
        }).collect(Collectors.toList());

        orders.setId(orderId);
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setStatus(2);
        orders.setAmount(new BigDecimal(amount.get()));//总金额
        orders.setUserId(userId);
        orders.setNumber(String.valueOf(orderId));
        orders.setUserName(user.getName());
        orders.setConsignee(addressBook.getConsignee());
        orders.setPhone(addressBook.getPhone());
        orders.setAddress((addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName())
                + (addressBook.getCityName() == null ? "" : addressBook.getCityName())
                + (addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName())
                + (addressBook.getDetail() == null ? "" : addressBook.getDetail()));
        //向订单表插入数据，一条数据
        this.save(orders);

        //向订单明细表插入数据，多条数据
        orderDetailService.saveBatch(orderDetails);

        //清空购物车数据
        shoppingCartService.remove(queryWrapper);

    }
}
