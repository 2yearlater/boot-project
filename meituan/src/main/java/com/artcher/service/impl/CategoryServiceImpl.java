package com.artcher.service.impl;

import com.artcher.domain.Category;
import com.artcher.domain.Dish;
import com.artcher.domain.Setmeal;
import com.artcher.exception.CustomException;
import com.artcher.mapper.CategoryMapper;
import com.artcher.service.CategoryService;
import com.artcher.service.DishService;
import com.artcher.service.SetmealService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper,Category> implements CategoryService {
    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;

    /**
     * 删除套餐方法
     */
    @Override
    public void remove(Long id) {
        //查询当前分类是否关联了菜品,关联就抛出异常
        LambdaQueryWrapper<Dish> dishQueryWrapper = new LambdaQueryWrapper();
        //添加查询条件
        dishQueryWrapper.eq(Dish::getCategoryId,id);
        //查询
        int count = dishService.count(dishQueryWrapper);
        //判断
        if (count >0){
            //查询到关联菜品,抛出异常
            throw new CustomException("有关联菜品,无法删除");
        }

        //查询当前分类是否关联了套餐,关联就抛出异常
        LambdaQueryWrapper<Setmeal> setmealQueryWrapper = new LambdaQueryWrapper();
        setmealQueryWrapper.eq(Setmeal::getCategoryId,id);
        int count1 = setmealService.count(setmealQueryWrapper);
        if (count1 >0){
            //查询到关联菜品,抛出异常
            throw new CustomException("有关联套餐,无法删除");
        }

        super.removeById(id);
    }
}
