package com.increff.pos.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ReportUiController extends AbstractUiController{

    @RequestMapping(value = "/ui/report")
    public ModelAndView home() {
        return mav("reports.html");
    }

    @RequestMapping(value = "/ui/report/brand")
    public ModelAndView brand(){
        return mav("brand-report.html");
    }

    @RequestMapping(value = "/ui/report/inventory")
    public ModelAndView product(){
        return mav("inventory-report.html");
    }

    @RequestMapping(value = "/ui/report/sales")
    public ModelAndView inventory(){return mav("sales-report.html");}

    @RequestMapping(value = "/ui/report/day-on-day")
    public ModelAndView order(){return mav("day-on-day-sales-report.html");}

}
