package com.artcher.controller;

import com.artcher.common.R;
import com.artcher.domain.Employee;
import com.artcher.domain.User;
import com.artcher.service.UserService;
import com.artcher.util.SMSUtils;
import com.artcher.util.ValidateCodeUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;

    //发送短信
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user,HttpServletRequest request){
        if (user.getPhone() != null && !"".equals(user.getPhone())){
            //生成4位随机验证码
            Integer code = ValidateCodeUtils.generateValidateCode(4);
            //SMSUtils1)系统首页,.sendMessage("singName","{code}",phone,code);
            log.info("code:"+code);
            request.getSession().setAttribute("phone",code);

            return R.success("成功发送验证码");
        }

        return R.error("发送验证码失败");
    }

    /**
     * 用户登陆方法
     * @param
     * @param map
     * @return
     */
    @PostMapping("/login")
    public R<User> login(HttpServletRequest request, @RequestBody Map map){
        //获取手机号和验证码
        String phone = (String) map.get("phone");
        String code1 = (String) map.get("code");

        Integer code = Integer.parseInt(code1);

        //从session中获取验证码
        Integer sessionCode = (Integer) request.getSession().getAttribute("phone");

        if (code != null && sessionCode != null){
            //判读验证码是否一致
            if (code.equals(sessionCode)){
                LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(User::getPhone,phone);
                User user = userService.getOne(queryWrapper);

                if (user == null){
                    //自动注册新用户
                    user = new User();
                    user.setPhone(phone);
                    user.setStatus(1);
                    userService.save(user);
                }

                request.getSession().setAttribute("user",user.getId());

                return R.success(user);
            }
        }

        return R.error("验证码输入错误");
    }


}
