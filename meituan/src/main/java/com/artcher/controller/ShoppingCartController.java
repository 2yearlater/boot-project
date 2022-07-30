package com.artcher.controller;

import com.artcher.common.BaseContext;
import com.artcher.common.R;
import com.artcher.domain.DishFlavor;
import com.artcher.domain.ShoppingCart;
import com.artcher.service.DishFlavorService;
import com.artcher.service.ShoppingCartService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.net.BCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import com.artcher.domain.ShoppingCart;

import java.awt.datatransfer.DataFlavor;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/shoppingCart")
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private DishFlavorService dishFlavorServicel;


    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart){
        //获取当前登陆用户的Id
        Long userId = BaseContext.getCurrentId();
        shoppingCart.setUserId(userId);

        //查询当前添加的菜品或者套餐是否在购物车中存在
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,userId);
        queryWrapper.eq(shoppingCart.getDishId()!=null,ShoppingCart::getDishId,shoppingCart.getDishId());
        queryWrapper.eq(shoppingCart.getSetmealId()!=null,ShoppingCart::getSetmealId,shoppingCart.getSetmealId());

        ShoppingCart one = shoppingCartService.getOne(queryWrapper);

        if (one != null){
            //有信息,用存好的信息
            Integer number = one.getNumber();
            one.setNumber(number+1);
            shoppingCartService.updateById(one);
            return R.success(one);
        }

        //数据库中没有信息就用传入的信息构造一个对象
        shoppingCart.setNumber(1);
        shoppingCart.setCreateTime(LocalDateTime.now());
        shoppingCartService.save(shoppingCart);

        return R.success(shoppingCart);
    }

    /**
     * 通过userID获取当前登陆用户的购物车信息
     * @return
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> list(){
        //获取UserId
        Long userId = BaseContext.getCurrentId();

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,userId);
        List<ShoppingCart> list = shoppingCartService.list(queryWrapper);

        return R.success(list);
    }

    /**
     * 通过userId清空当前用户的购物车信息
     * @return
     */
    @DeleteMapping("/clean")
    public R<String> clean(){
        //获取用户id
        Long userId = BaseContext.getCurrentId();

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,userId);
        shoppingCartService.remove(queryWrapper);

        return R.success("清空购物车成功");
    }

    @PostMapping("/sub")
    public R<ShoppingCart> sub(@RequestBody ShoppingCart shoppingCart){
        //获取当前登陆用户的Id
        Long userId = BaseContext.getCurrentId();
        shoppingCart.setUserId(userId);

        //查询当前添加的菜品或者套餐是否在购物车中存在
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,userId);
        queryWrapper.eq(shoppingCart.getDishId()!=null,ShoppingCart::getDishId,shoppingCart.getDishId());
        queryWrapper.eq(shoppingCart.getSetmealId()!=null,ShoppingCart::getSetmealId,shoppingCart.getSetmealId());

        ShoppingCart one = shoppingCartService.getOne(queryWrapper);


        //有信息,用存好的信息
        Integer number = one.getNumber();
        if (number > 1) {
            one.setNumber(number - 1);
            shoppingCartService.updateById(one);
            return R.success(one);
        }

        shoppingCartService.remove(queryWrapper);

        return R.success(shoppingCart);
    }


}
