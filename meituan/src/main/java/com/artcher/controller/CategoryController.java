package com.artcher.controller;

import com.artcher.common.R;
import com.artcher.domain.Category;
import com.artcher.domain.Dish;
import com.artcher.domain.Setmeal;
import com.artcher.exception.CustomException;
import com.artcher.service.CategoryService;
import com.artcher.service.DishService;
import com.artcher.service.SetmealService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@Slf4j
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;

    /**
     * 添加分类,菜品分类和套餐分类一个方法,通过传入的type来区分是菜品还是套餐
     * @param category
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody Category category){
        categoryService.save(category);

        return R.success("添加种类成功");
    }

    /**
     * 分页查询
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(@RequestParam Integer page,@RequestParam Integer pageSize){
        //创建page类
        Page pageInfo = new Page(page,pageSize);

        //创建条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper();

        //添加查询条件
        queryWrapper.orderByDesc(Category::getSort);

        //调用service的查询方法
        categoryService.page(pageInfo,queryWrapper);

        return R.success(pageInfo);
    }

    /**
     * 删除菜品
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestParam Long ids){
        log.info("id:"+ids);
        //判断是否可以删除
        categoryService.remove(ids);

        return R.success("删除成功");
    }

    /**
     * 修改分类操作
     * @param category
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody Category category){
        log.info("修改信息:"+category);
        categoryService.updateById(category);

        return R.success("修改成功");
    }

    /**
     * 根据type类型,返回菜品或者套餐list,响应在前端下拉框中,当客户选择
     * @param
     * @return
     */
    @GetMapping("/list")
    public R<List<Category>> getDishList(Category category){
        //创建条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper();
        //添加条件
        queryWrapper.eq(category.getType() != null,Category::getType,category.getType());
        //添加查询条件
        queryWrapper.orderByDesc(Category::getSort).orderByDesc(Category::getUpdateTime);
        //调用service的list方法
        List<Category> list = categoryService.list(queryWrapper);

        return R.success(list);
    }


}
