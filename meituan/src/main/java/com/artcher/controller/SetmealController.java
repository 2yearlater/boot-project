package com.artcher.controller;

import com.artcher.common.R;
import com.artcher.domain.Category;
import com.artcher.domain.Dish;
import com.artcher.domain.Setmeal;
import com.artcher.dto.SetmealDto;
import com.artcher.service.CategoryService;
import com.artcher.service.SetmealService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@Slf4j
@RequestMapping("setmeal")
public class SetmealController {
    @Autowired
    private SetmealService setmealService;

    @Autowired
    private CategoryService categoryService;

    /**
     * 添加新套餐方法
     * @param setmealDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto){
        setmealService.saveWithDish(setmealDto);

        return R.success("添加套餐成功");
    }

    /**
     * 分页查询,展示套餐信息,结合category展示套餐分类名称
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(@RequestParam Integer page,@RequestParam Integer pageSize,@RequestParam(required = false)String name){
        //分页查询构造器
        Page<Setmeal> setmealPage = new Page(page,pageSize);
        Page<SetmealDto> setmealDtoPage = new Page<>();

        //创建条件构造器
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper();
        //添加模糊查询条件
        queryWrapper.like(name!=null, Setmeal::getName,name);
        //添加排序条件
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        //调用page分页方法
        setmealService.page(setmealPage);

        //复制setmealPage属性到setmealDtoPage,除了records(存放的Setmeal信息,没有套餐名称,需用SetmealDto替换)
        BeanUtils.copyProperties(setmealPage,setmealDtoPage,"records");
        //获取SetmealList,存放Setmeal
        List<Setmeal> records = setmealPage.getRecords();

        //遍历,赋值 SetmealList,item是Setmeal
        List<SetmealDto> list = records.stream().map(item -> {
            SetmealDto setmealDto = new SetmealDto();

            //获取套餐分类的id
            Long categoryId = item.getCategoryId();
            //根据categoryId获取categoryName
            Category category = categoryService.getById(categoryId);
            String categoryName = category.getName();
            if (!StringUtils.isEmpty(categoryName)){
                //给SetmealDto复制套餐分类名称
                setmealDto.setCategoryName(categoryName);
                //复制Setmeal的属性到SetmealDto
                BeanUtils.copyProperties(item,setmealDto);
                return setmealDto;
            }
            return setmealDto;
        }).collect(Collectors.toList());

        //给SetmealDtoPage的records赋值
        setmealDtoPage.setRecords(list);

        return R.success(setmealDtoPage);

    }

    /**
     * 删除套餐或批量删除套餐,传入套餐的id
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids){
        setmealService.deleteWithDish(ids);

        return R.success("删除套餐成功");
    }

    /**
     * 启售停售方法
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> updateStatus(@PathVariable Integer status,@RequestParam List<Long> ids){
        //通过id查询之前的套餐信息
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.in(ids!=null,Setmeal::getId,ids);
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        List<Setmeal> list = setmealService.list(queryWrapper);

        if (list.size() != 0 && list != null){
            //给setmean的status赋值
            list.stream().map(item -> {
                item.setStatus(status);
                return item;
            }).collect(Collectors.toList());

            //调用方法改变状态
            setmealService.remove(queryWrapper);
            setmealService.saveBatch(list);

            return R.success("状态变成成功");
        }

        return R.error("修改状态失败");
    }

    /**
     * 根据套餐id获取套餐及菜品数据
     * @param id
     * @return
     */
    @GetMapping("{id}")
    public R<SetmealDto> getById(@PathVariable Long id){

        SetmealDto setmealDto = setmealService.getWithDish(id);

        return R.success(setmealDto);
    }

    /**
     * 更新套餐信息和对应的dish信息
     * @param setmealDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody SetmealDto setmealDto){
        setmealService.updateWithDish(setmealDto);

        return R.success("更新状态成功");
    }

    /**
     * 根据categoryId,status查询所有的菜品
     * @param
     * @return
     */
    @GetMapping("/list")
    public R<List<Setmeal>> getDishList(Setmeal setmeal){
        //创建条件构造器
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper();
        //添加条件, =id
        queryWrapper.eq(setmeal.getCategoryId()!=null,Setmeal::getCategoryId, setmeal.getCategoryId());
        queryWrapper.eq(setmeal.getStatus()!=null, Setmeal::getStatus, setmeal.getStatus());
        //添加排序条件
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        //查询 select * from dish where categoryId = {} orderBy sort,updateTime
        List<Setmeal> list = setmealService.list(queryWrapper);

        return R.success(list);
    }

}
