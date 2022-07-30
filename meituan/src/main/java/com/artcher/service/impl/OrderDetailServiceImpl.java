package com.artcher.service.impl;

import com.artcher.domain.OrderDetail;
import com.artcher.mapper.OrderDetailMapper;
import com.artcher.service.OrderDetailService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {
}
