package com.artcher.controller;

import com.artcher.common.BaseContext;
import com.artcher.common.R;
import com.artcher.domain.Employee;
import com.artcher.service.EmployeeService;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;


@RestController
@RequestMapping("/employee")
@Slf4j
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    /**
     * 登陆方法,返回模型类R
     *  将页面传来的数据MD5加密(实际开发前台会传加密后的数据)
     *  根据username查询数据库
     *  没有查询到数据,返回登陆失败结果
     *  匹配密码,不一致则返回密码错误失败结果
     *  查看用户状态,为0则禁用,返回失败结果
     *  登陆成功,将用户id存入session中
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){
        // 将页面传来的数据MD5加密(实际开发前台会传加密后的数据)
        String password = employee.getPassword();
        String md5Password = DigestUtils.md5DigestAsHex(password.getBytes());
        // 根据username查询数据库
        //mybatis-plug中的查数据库用法(不需要在mapper中写内容,直接在service实现类中写方法即可)
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee queryEmployee = employeeService.getOne(queryWrapper);
        // 没有查询到数据,返回登陆失败结果
        if (queryEmployee == null){
            return R.error("用户名输入错误");
        }
        // 匹配密码,不一致则返回密码错误失败结果
        if (!md5Password.equals(queryEmployee.getPassword())){
            return R.error("密码错误,请重新输入");
        }
        // 查看用户状态,为0则禁用,返回失败结果
        if (queryEmployee.getStatus() == 0){
            return R.error("用户以被禁用,无法访问");
        }
        // 登陆成功,将用户id存入session中
        HttpSession session = request.getSession();
        session.setAttribute("employee",queryEmployee.getId());

        return R.success(queryEmployee);
    }

    /**
     * 登出操作
     *  清除session中当前员工的id
     *  退出
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        //清除session中当前登陆员工的id
        request.getSession().removeAttribute("employee");

        return R.success("退出成功");
    }

    /**
     * 添加用户方法,不能添加账号相同的用户,否则抛出异常
     * @param request
     * @param employee
     * @return
     */
    @PostMapping
    public R<String> add(HttpServletRequest request, @RequestBody Employee employee){
        log.info(Thread.currentThread().getName());

        String password = DigestUtils.md5DigestAsHex("123456".getBytes());
        //设置默认密码,创建时间,创建人
        employee.setPassword(password);
        // employee.setCreateTime(LocalDateTime.now());
        // employee.setUpdateTime(LocalDateTime.now());
        // employee.setCreateUser((Long) request.getSession().getAttribute("employee"));
        // employee.setUpdateUser((Long) request.getSession().getAttribute("employee"));

        //添加失败会报异常,使用全局一场处理
        employeeService.save(employee);

        return R.success("新增成功");
    }

    /**
     * 分页方法,使用mybatisPlus插件
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @RequestMapping("/page")
    public R<Page> page(@RequestParam Integer page,@RequestParam Integer pageSize,@RequestParam(required = false) String name){
        //创建分页构造器
        Page pageInfo = new Page(page,pageSize);

        //创造构造条件
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper();
        //模糊查询
        queryWrapper.like(!StringUtils.isEmpty(name),Employee::getName,name);
        //排序
        queryWrapper.orderByDesc(Employee::getUpdateTime);

        //执行分页查询
        employeeService.page(pageInfo,queryWrapper);

        return R.success(pageInfo);
    }

    /**
     * 更新用户信息方法(编辑和更改状态共用的方法)
     *  ①设修改时间和修改人
     *  ②设置id
     *  ③调用service的更新方法
     *  ④返回 更新成功
     *
     *  由于用户id使用雪花算法,因此在前段中19位数字有精度损失,需要在返回数据时将用户id转换位String
     *      -->响应数据时,将用户打包位json数组的格式来响应
     * @param employee
     * @return
     */
    @PutMapping
    public R<String> update(HttpServletRequest request,@RequestBody Employee employee){
        log.info(Thread.currentThread().getName());

        log.info("employee=" + employee);

        // 获取用户id
        // Long empId = BaseContext.getCurrentId();
        //
        // 设修改时间和修改人
        // employee.setUpdateTime(LocalDateTime.now());
        // employee.setUpdateUser(empId);

        // 调用service的更新方法
        employeeService.updateById(employee);
        // 返回 更新成功

        return R.success("更新用户状态成功");

    }

    /**
     * 根据传来的id查询用户信息,响应到页面,在修改页面中回显
     * @param id
     * @return
     */
    @RequestMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        Employee employee = employeeService.getById(id);
        if (employee != null){
            return R.success(employee);
        }
        return R.error("查询用户失败");
    }

}
