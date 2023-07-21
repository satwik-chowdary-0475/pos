package com.increff.pos.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.increff.pos.dto.UserDto;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.increff.pos.model.form.LoginForm;
import com.increff.pos.service.ApiException;
import io.swagger.annotations.ApiOperation;

@RestController
@Api
public class LoginController {

    @Autowired
    private UserDto userDto;

    @ApiOperation(value = "Logs in a user")
    @RequestMapping(path = "/session/login", method = RequestMethod.POST)
    public void login(HttpServletRequest request, @RequestBody LoginForm loginForm) throws ApiException {
        userDto.login(request, loginForm);
    }

    @RequestMapping(path = "/session/logout", method = RequestMethod.GET)
    public ModelAndView logout(HttpServletRequest request, HttpServletResponse response) {
        return userDto.logout(request);
    }

}