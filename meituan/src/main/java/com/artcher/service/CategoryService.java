package com.artcher.service;

import com.artcher.domain.Category;
import com.baomidou.mybatisplus.extension.service.IService;

public interface CategoryService extends IService<Category> {
    public void remove(Long id);
}
