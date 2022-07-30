package com.artcher.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
public class MyMetaObjectHandler implements MetaObjectHandler {
    /**
     * 插入时设置公共属性的方法
     * @param metaObject
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        log.info(Thread.currentThread().getName());

        Long currentId = BaseContext.getCurrentId();
        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("createUser",currentId);
        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("updateUser",currentId);
    }

    /**
     * 更新时设置公共属性的方法,以后就不需要手动设公共属性
     * @param metaObject
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        Long currentId = BaseContext.getCurrentId();
        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("updateUser",currentId);
    }
}
