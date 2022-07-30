package com.artcher.common;

import lombok.extern.slf4j.Slf4j;

/**
 * 工具类,基于ThreadLocal实现
 *  ThreadLocal:jdk提供的一个类,在同一个线程之间开辟一个空间
 *  一个线程中,共享空间中的数据
 *  每个线程的这个空间都是独立的
 */
@Slf4j
public class BaseContext{
    private static ThreadLocal<Long>  threadLocal = new ThreadLocal<Long>();


    public static Long getCurrentId() {
        log.info(Thread.currentThread().getName());

        Long id = threadLocal.get();
        return id;
    }


    public static void setCurrentId(Long id) {
        log.info(Thread.currentThread().getName());

        threadLocal.set(id);
    }
}
