package com.increff.pos.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class UiController extends AbstractUiController {

    @RequestMapping(value = "")
    public ModelAndView index() {
        return mav("index.html");
    }

    @RequestMapping(value = "/login")
    public ModelAndView login() {
        return mav("login.html");
    }

    @RequestMapping(value = "/home")
    public ModelAndView home() {
        return mav("home.html");
    }

    @RequestMapping(value = "/brands")
    public ModelAndView brand() {
        return mav("brand.html");
    }

    @RequestMapping(value = "/products")
    public ModelAndView product() {
        return mav("product.html");
    }

    @RequestMapping(value = "/inventory")
    public ModelAndView inventory() {
        return mav("inventory.html");
    }

    @RequestMapping(value = "/orders")
    public ModelAndView order() {
        return mav("order.html");
    }

    @RequestMapping(value = "/orders/{orderCode}")
    public ModelAndView orderItem(@PathVariable String orderCode) {
        return mav("order-item.html", orderCode);
    }

    @RequestMapping(value = "/reports")
    public ModelAndView reports() {
        return mav("reports.html");
    }


}
