package com.artcher.mapper;

import com.artcher.domain.Employee;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * mybatis-plug中,mapper类继承BaseMapper(提供常见的数据库操作方法)
 */
@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
}
