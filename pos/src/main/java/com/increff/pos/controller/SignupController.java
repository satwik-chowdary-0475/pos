package com.increff.pos.controller;

import com.increff.pos.dto.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.increff.pos.model.data.InfoData;
import com.increff.pos.model.form.UserForm;
import com.increff.pos.service.ApiException;

import io.swagger.annotations.ApiOperation;

@Controller
public class SignupController extends AbstractUiController {

    @Autowired
    private UserDto userDto;
    @Autowired
    private InfoData info;

    @ApiOperation(value = "Initializes application")
    @RequestMapping(path = "/site/signup", method = RequestMethod.GET)
    public ModelAndView showPage(UserForm form) throws ApiException {
        info.setMessage("");
        return mav("signup.html");
    }

    @ApiOperation(value = "Initializes application")
    @RequestMapping(path = "/site/signup", method = RequestMethod.POST)
    public ModelAndView signupSite(UserForm userForm) throws ApiException {
        userDto.add(userForm);
        info.setMessage("User added successfully!");
        return mav("login.html");
    }



}