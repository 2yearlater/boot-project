package com.artcher.controller;

import com.artcher.common.R;
import com.artcher.domain.Category;
import com.artcher.domain.Dish;
import com.artcher.domain.DishFlavor;
import com.artcher.dto.DishDto;
import com.artcher.service.CategoryService;
import com.artcher.service.DishFlavorService;
import com.artcher.service.DishService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;

import javax.websocket.server.PathParam;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/dish")
public class DishController {
    @Autowired
    private DishService dishService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private DishFlavorService dishFlavorService;

    /**
     * 根据categoryId查询所有的菜品,
     * 在前端展示需要口味信息,因此返回vo类
     * @param
     * @return
     */
    @GetMapping("/list")
    public R<List<DishDto>> getDishList(Dish dish){
        List<DishDto> dishDtoList = null;

        //创建条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper();
        //添加条件, =id
        queryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId, dish.getCategoryId());
        queryWrapper.eq(dish.getStatus()!=null,Dish::getStatus,dish.getStatus());
        //添加排序条件
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        //查询 select * from dish where categoryId = ? and status = ? orderBy sort,updateTime
        List<Dish> list = dishService.list(queryWrapper);

        //构造dishDtoList中的dish属性
        dishDtoList = list.stream().map(item -> {
            DishDto dishDto = new DishDto();
            //包dish的信息拷贝到dishDto
            BeanUtils.copyProperties(item,dishDto);

            Long dishId = item.getId();
            LambdaQueryWrapper<DishFlavor> flavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
            flavorLambdaQueryWrapper.eq(DishFlavor::getDishId,dishId);
            List<DishFlavor> flavors = dishFlavorService.list(flavorLambdaQueryWrapper);

            dishDto.setFlavors(flavors);


            return dishDto;
        }).collect(Collectors.toList());

        return R.success(dishDtoList);
    }

    /**
     * 保存新菜品方法,因为前端传来的输出除了Dish的属性还有口味List<DishFlavor>
     *     因此封装到DishDto类中
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
        dishService.saveWithFlavor(dishDto);

        return R.success("添加菜品成功");
    }

    /**
     * 菜品的分页方法,因为返回值需要一个categoryName(分类名称),
     * 因此返回Vo对象
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(@RequestParam Integer page,@RequestParam Integer pageSize,@RequestParam(required = false) String name){
        //创建分页构造器
        Page<Dish> dishPage = new Page<>(page,pageSize);
        dishPage.setOptimizeCountSql(true);

        Page<DishDto> dtoPage = new Page<>();

        //创建条件查询构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper();
        //添加模糊查询条件
        queryWrapper.like(name != null,Dish::getName,name);
        //添加排序条件
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        //调用分页方法
        dishService.page(dishPage, queryWrapper);

        //将dishPage属性拷贝到dtoPage中 忽略records(records存放的是dishList)
        BeanUtils.copyProperties(dishPage,dtoPage,"records");

        List<Dish> dishList = dishPage.getRecords();
        //循环遍历给DishDtoList中的每个DishDto赋值
        List<DishDto> dishDtoList = dishList.stream().map(item -> {
            //item表示DishList中的Dish对象

            //获取dish对象的CategoryId
            Long categoryId = item.getCategoryId();

            //获取category对象,获取categoryName
            Category category = categoryService.getById(categoryId);
            String categoryName = category.getName();

            DishDto dishDto = new DishDto();
            dishDto.setCategoryName(categoryName);

            //赋值Dish的值到DishDto
            BeanUtils.copyProperties(item,dishDto);

            return dishDto;
        }).collect(Collectors.toList());

        //给DishDtoPage的records赋值
        dtoPage.setRecords(dishDtoList);

        return R.success(dtoPage);
    }

    /**
     * 根据dishId获取dish数据以及口味数据,回显到修改菜品信息页面
     * @param id
     * @return
     */
    @RequestMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id){
        DishDto dishDto = dishService.geyByIdWithFlavor(id);

        return R.success(dishDto);
    }

    /**
     * 根据传来的菜品及口味信息,更新数据
     * 当前端使用json传递数据使用@RequestBody接收
     * 否则可以实际用实体类接收(mvc自动将数据封装到实体类)
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        dishService.updateWithFlavor(dishDto);

        return R.success("更新成功");
    }

}
