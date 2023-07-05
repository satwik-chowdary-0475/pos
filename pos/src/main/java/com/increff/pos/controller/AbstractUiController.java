package com.increff.pos.controller;

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
        // Get current user
        UserPrincipal principal = SecurityUtil.getPrincipal();

        infoData.setEmail(principal == null ? "" : principal.getEmail());
        infoData.setRole(getRole());

        // Set info
        ModelAndView mav = new ModelAndView(page);
        mav.addObject("info", infoData);
        mav.addObject("baseUrl", baseUrl);
        return mav;
    }

    protected ModelAndView mav(String page, String orderCode) {
        // Get current user
        UserPrincipal principal = SecurityUtil.getPrincipal();

        infoData.setEmail(principal == null ? "" : principal.getEmail());
        infoData.setRole(getRole());

        // Set info
        ModelAndView mav = new ModelAndView(page);
        mav.addObject("info", infoData);
        mav.addObject("baseUrl", baseUrl);
        mav.addObject("orderCode", orderCode);
        return mav;
    }

    private String getRole(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();;
        if(Objects.isNull(authentication) || Objects.isNull(authentication.getAuthorities())) return "";
        List<String> authorities = authentication.getAuthorities()
                .stream()
                .map(Object::toString)
                .collect(Collectors.toList());
        if(authorities.contains("supervisor")){
            return "supervisor";
        }
        return "operator";
    }

}