package com.increff.pos.dto;

import com.increff.pos.Helper;
import com.increff.pos.model.form.UserForm;
import com.increff.pos.pojo.UserPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.UserService;
import com.increff.pos.spring.AbstractUnitTest;
import com.increff.pos.pojo.UserRole;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static junit.framework.TestCase.assertEquals;

public class UserDtoTest extends AbstractUnitTest {
    @Autowired
    private UserDto userDto;
    @Autowired
    private UserService userService;

    @Test
    public void TestInsertOperator() throws ApiException{
        UserForm userForm = Helper.createUserForm("a@gmail.com","1234");
        userDto.signup(userForm);
        List<UserPojo> userPojoList = userService.getAllUsers();
        assertEquals(userPojoList.size(),1);
        assertEquals(userPojoList.get(0).getRole(),UserRole.OPERATOR);
    }

    @Test
    public void TestInsertSupervisor() throws ApiException{
        UserForm userForm = Helper.createUserForm("abc@gmail.com","1234");
        userDto.signup(userForm);
        List<UserPojo> userPojoList = userService.getAllUsers();
        assertEquals(userPojoList.size(),1);
        assertEquals(userPojoList.get(0).getRole(), UserRole.SUPERVISOR);
    }

}
