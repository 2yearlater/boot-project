package com.artcher.service.impl;

import com.artcher.domain.AddressBook;
import com.artcher.mapper.AddressBookMapper;
import com.artcher.service.AddressBookService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {
}
