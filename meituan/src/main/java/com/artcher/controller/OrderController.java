package com.artcher.controller;

import com.artcher.common.R;
import com.artcher.domain.Orders;
import com.artcher.dto.OrdersDto;
import com.artcher.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("order")
@Slf4j
public class OrderController {
    @Autowired
    private OrdersService ordersService;

    @PostMapping("submit")
    public R<String> save(@RequestBody Orders orders){
        ordersService.saveWithDetail(orders);

        return R.success("下单成功");
    }
}
