package com.artcher.common;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class R<T> {
    private Integer code; //1表示访问成功,0表示访问失败
    private String msg;
    private T data;
    //动态数据
    private Map map = new HashMap();

    /**
     * 访问成功方法
     * @param object
     * @param <T>
     * @return
     */
    public static <T>R<T> success(T object){
        R<T> r = new R<T>();
        r.code = 1;
        r.data = object;

        return r;
    }

    /**
     * 访问失败方法
     * @param msg
     * @param <T>
     * @return
     */
    public static <T> R<T> error(String msg){
        R<T> r = new R<T>();
        r.code = 0;
        r.msg = msg;

        return r;
    }

    /**
     * 添加动态数据
     * @param key
     * @param value
     * @return
     */
    public R<T> add(String key,Object value){
        this.map.put(key,value);

        return this;
    }

}
