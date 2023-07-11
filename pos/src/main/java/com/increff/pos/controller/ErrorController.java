package com.increff.pos.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ErrorController {
    @RequestMapping("/site/error-403")
    public ModelAndView handle403Error() {
        return new ModelAndView("error-403.html"); // Set the name of your custom error page file
    }
}
