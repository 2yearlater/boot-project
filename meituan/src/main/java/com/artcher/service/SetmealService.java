package com.artcher.service;

import com.artcher.domain.Setmeal;
import com.artcher.dto.SetmealDto;
import com.baomidou.mybatisplus.extension.service.IService;


import java.util.List;

public interface SetmealService extends IService<Setmeal> {
    public void saveWithDish(SetmealDto setmealDto);

    public void deleteWithDish(List<Long> ids);

    public SetmealDto getWithDish(Long id);

    public void updateWithDish(SetmealDto setmealDto);
}
