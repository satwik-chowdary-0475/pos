package com.increff.pos.controller;

import com.increff.pos.pojo.UserRole;
import com.increff.pos.util.AdminUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.ModelAndView;

import com.increff.pos.model.data.InfoData;
import com.increff.pos.util.SecurityUtil;
import com.increff.pos.util.UserPrincipal;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Controller
public abstract class AbstractUiController {

    @Autowired
    private InfoData infoData;

    @Value("${app.baseUrl}")
    private String baseUrl;

    protected ModelAndView mav(String page) {
        UserPrincipal principal = SecurityUtil.getPrincipal();
        infoData.setEmail(Objects.isNull(principal) ? "" : principal.getEmail());
        infoData.setRole(AdminUtil.getRole().toLowerCase());
        ModelAndView mav = new ModelAndView(page);
        mav.addObject("info", infoData);
        mav.addObject("baseUrl", baseUrl);
        return mav;
    }

    protected ModelAndView mav(String page, String orderCode) {
        UserPrincipal principal = SecurityUtil.getPrincipal();
        infoData.setEmail(Objects.isNull(principal) ? "" : principal.getEmail());
        infoData.setRole(AdminUtil.getRole().toLowerCase());
        ModelAndView mav = new ModelAndView(page);
        mav.addObject("info", infoData);
        mav.addObject("baseUrl", baseUrl);
        mav.addObject("orderCode", orderCode);
        return mav;
    }


}