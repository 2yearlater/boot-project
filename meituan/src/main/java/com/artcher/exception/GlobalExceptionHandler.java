package com.artcher.exception;

import com.artcher.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理器
 * @ControllerAdvice(annotations = {RestController.class, Controller.class})
 * 拦截添加了指定注解类的控制类
 */
@ControllerAdvice(annotations = {RestController.class, Controller.class})
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler()
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException ex){
        //打印日志信息
        log.error(ex.getMessage());

        //拼接一场信息
        String [] exceptions = ex.getMessage().split(" ");
        String msg = exceptions[2] + "账号已存在";

        return R.error(msg);
    }

    /**
     * 处理自定义业务异常的方法
     * @param ex
     * @return
     */
    @ExceptionHandler
    public R<String> customExceptionHandler(Exception ex){
        return R.error(ex.getMessage());
    }
}
