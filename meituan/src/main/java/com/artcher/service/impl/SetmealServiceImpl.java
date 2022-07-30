package com.artcher.service.impl;

import com.artcher.domain.Setmeal;
import com.artcher.domain.SetmealDish;
import com.artcher.dto.SetmealDto;
import com.artcher.exception.CustomException;
import com.artcher.mapper.SetmealMapper;
import com.artcher.service.SetmealDishService;
import com.artcher.service.SetmealService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {
    @Autowired
    private SetmealDishService setmealDishService;

    /**
     * 添加套餐的同时,添加菜品和套餐的关系
     * @param setmealDto
     */
    @Override
    public void saveWithDish(SetmealDto setmealDto) {
        //调用service方法,添加Setmeal数据
        this.save(setmealDto);

        //获取套餐中的菜品信息List
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        //给DishList列表中对应的对象添加SetmealId
        setmealDishes.stream().map(item -> {
            //给Setmeal对象赋值SetmealId,
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        //调用saveBench方法,将套餐和菜品关系批量保存到表中
        setmealDishService.saveBatch(setmealDishes);
    }

    /**
     * 批量删除套餐信息
     * @param ids
     */
    @Override
    public void deleteWithDish(List<Long> ids) {
        //判断套餐状态是否为启售状态
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper();
        //添加查询条件
        queryWrapper.in(ids!=null,Setmeal::getId,ids);
        //添加查询条件,状态1:启售状态
        queryWrapper.eq(Setmeal::getStatus,1);
        //select count(*) from setmeal where id in() and status = 1;
        int count = this.count(queryWrapper);
        if (count>0){
            //选中的套餐中有正在售卖套餐,抛出异常
            throw new CustomException("套餐正在售卖,不能删除");
        }

        //进行删除套餐操作
        this.removeByIds(ids);

        //删除套餐对应的关系表内容
        LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealDishLambdaQueryWrapper.in(SetmealDish::getSetmealId,ids);
        setmealDishService.remove(setmealDishLambdaQueryWrapper);

    }

    /**
     * 根据id差套餐具体信息以及菜品信息
     * @param id
     * @return
     */
    @Override
    public SetmealDto getWithDish(Long id) {
        if (id != null) {
            //查询套餐数据
            Setmeal setmeal = this.getById(id);
            //查询套餐对应的Dish数据
            LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SetmealDish::getSetmealId, id);
            //select * from setmeal_dish where setmeal_id = ?

            List<SetmealDish> list = setmealDishService.list(queryWrapper);

            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(setmeal, setmealDto);
            setmealDto.setSetmealDishes(list);

            return setmealDto;
        }
       return null;
    }

    @Override
    public void updateWithDish(SetmealDto setmealDto) {
        //调用service方法,更新setmeal数据
        this.updateById(setmealDto);

        //获取套餐中的菜品信息List
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        //给DishList列表中对应的对象添加SetmealId
        setmealDishes.stream().map(item -> {
            //给Setmeal对象赋值SetmealId,
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,setmealDto.getId());
        //删除套餐对应的Dish信息
        setmealDishService.remove(queryWrapper);
        //调用saveBench方法,将套餐和菜品关系批量保存到表中
        setmealDishService.saveBatch(setmealDishes);
    }


}
