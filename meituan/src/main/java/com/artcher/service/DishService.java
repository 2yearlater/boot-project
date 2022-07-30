package com.artcher.service;

import com.artcher.domain.Dish;
import com.artcher.dto.DishDto;
import com.baomidou.mybatisplus.extension.service.IService;

public interface DishService extends IService<Dish> {
    //保存Dish和口味信息
    public void saveWithFlavor(DishDto dishDto);

    //查询Dish以及对应的口味信息
    public DishDto geyByIdWithFlavor(Long id);

    //更新Dish以及对应的口味信息
    public void updateWithFlavor(DishDto dishDto);

}
