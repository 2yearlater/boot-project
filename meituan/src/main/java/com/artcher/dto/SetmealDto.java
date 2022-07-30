package com.artcher.dto;


import com.artcher.domain.Setmeal;
import com.artcher.domain.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
