package com.artcher.service;

import com.artcher.domain.Orders;
import com.baomidou.mybatisplus.extension.service.IService;

public interface OrdersService extends IService<Orders> {
    public void saveWithDetail(Orders orders);
}
