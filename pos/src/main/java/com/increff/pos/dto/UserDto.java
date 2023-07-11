package com.increff.pos.dto;

import com.increff.pos.dto.helper.HelperDto;
import com.increff.pos.model.form.UserForm;
import com.increff.pos.pojo.UserPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.UserService;
import com.increff.pos.util.AdminUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class UserDto {

    @Autowired
    private UserService userService;

    @Transactional
    public void add(UserForm userForm) throws ApiException {
        HelperDto.normalise(userForm);
        UserPojo userPojo = HelperDto.convert(userForm);
        userService.add(userPojo);
    }


}
