package com.artcher.service.impl;

import com.artcher.domain.ShoppingCart;
import com.artcher.mapper.ShoppingCartMapper;
import com.artcher.service.ShoppingCartService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {
}
