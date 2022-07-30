package com.artcher.controller;

import com.artcher.common.BaseContext;
import com.artcher.common.R;
import com.artcher.domain.AddressBook;
import com.artcher.service.AddressBookService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/addressBook")
public class AddressBookController {
    @Autowired
    private AddressBookService addressBookService;

    /**
     * 获取用户所有的地址信息
     * @return
     */
    @GetMapping("/list")
    public R<List<AddressBook>> list(){
        //获取用户id
        Long userId = BaseContext.getCurrentId();
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getUserId,userId);
        List<AddressBook> addressBooks = addressBookService.list(queryWrapper);

        return R.success(addressBooks);
    }

    /**
     * 获取用户的默认地址
     * @return
     */
    @GetMapping("/default")
    public R<AddressBook> getDefault(){
        //获取用户id
        Long userId = BaseContext.getCurrentId();
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getUserId,userId);
        queryWrapper.eq(AddressBook::getIsDefault,1);
        AddressBook addressBook = addressBookService.getOne(queryWrapper);

        return R.success(addressBook);
    }

    /**
     * 添加新的收获地址
     * @param addressBook
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody AddressBook addressBook){
        //获取用户id
        Long userId = BaseContext.getCurrentId();
        addressBook.setUserId(userId);
        addressBookService.save(addressBook);

        return R.success("添加成功");
    }



}
