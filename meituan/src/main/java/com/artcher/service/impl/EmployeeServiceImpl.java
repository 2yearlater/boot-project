package com.artcher.service.impl;

import com.artcher.domain.Employee;
import com.artcher.mapper.EmployeeMapper;
import com.artcher.service.EmployeeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * mybatis-plus service实现类继承SerivceImpl(提供了常见的方法)
 */
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper,Employee> implements EmployeeService{
}
