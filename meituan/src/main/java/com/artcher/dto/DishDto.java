package com.artcher.dto;


import com.artcher.domain.Dish;
import com.artcher.domain.DishFlavor;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

/**
 * 该类继承Dish,拥有dish的所有属性
 */
@Data
public class DishDto extends Dish {

    //口味list
    private List<DishFlavor> flavors = new ArrayList<>();

    //分类名称
    private String categoryName;

    //件数
    private Integer copies;
}
