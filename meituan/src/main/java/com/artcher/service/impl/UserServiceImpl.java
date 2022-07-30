package com.artcher.service.impl;

import com.artcher.domain.User;
import com.artcher.mapper.UserMapper;
import com.artcher.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
