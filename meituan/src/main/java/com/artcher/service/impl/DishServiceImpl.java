package com.artcher.service.impl;

import com.artcher.domain.Dish;
import com.artcher.domain.DishFlavor;
import com.artcher.dto.DishDto;
import com.artcher.mapper.DishMapper;
import com.artcher.service.DishFlavorService;
import com.artcher.service.DishService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    @Autowired
    private DishFlavorService dishFlavorService;

    /**
     * 保存菜品,dish属性以及Flavor属性
     * @param dishDto
     */
    @Override
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        //存储dish数据到dish表中
        this.save(dishDto);

        //获取菜品的id
        Long dishId = dishDto.getId();

        //获取DishDto中的Flavor List,
        List<DishFlavor> flavors = dishDto.getFlavors();
        //将每个list中DishFlavor赋值(name,value)  --> 只需要指定DishId,name->value前端已传到list中
        flavors.stream().map(item -> {
            //item就是遍历list获取的DishFlavor数据
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(dishDto.getFlavors());
    }

    /**
     * 查询Dish以及对应的口味信息
     * @param id
     * @return
     */
    @Override
    public DishDto geyByIdWithFlavor(Long id) {
        //根据Id查询Dish信息
        Dish dish = this.getById(id);
        DishDto dishDto = new DishDto();

        //创建条件构造器
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper();
        //添加查询条件 select * from dish where id={}
        queryWrapper.eq(DishFlavor::getDishId,id);
        //调用查询方法获取口味list
        List<DishFlavor> list = dishFlavorService.list(queryWrapper);

        //把dish的信息拷贝到dishDto中
        BeanUtils.copyProperties(dish,dishDto);

        //给DishDto设置口味list
        dishDto.setFlavors(list);

        //返回
        return dishDto;
    }

    /**
     * 更新Dish以及对应的口味信息
     * @param dishDto
     */
    @Override
    public void updateWithFlavor(DishDto dishDto) {
        //更新dish的信息
        this.updateById(dishDto);

        //获取dishId
        Long dishId = dishDto.getId();

        //给每个flavor对象的dishId赋值
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors.stream().map(item -> {
            //给每个flavor对象的dishId赋值
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());

        //创建条件构造器
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper();
        //添加查询条件
        queryWrapper.eq(DishFlavor::getDishId,dishId);
        //移除dishId对应的所有口味信息
        dishFlavorService.remove(queryWrapper);

        //调用更新方法,批量保存
        dishFlavorService.saveBatch(flavors);
    }
}
