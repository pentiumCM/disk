package com.cloud.disk.controller;

import com.cloud.disk.units.UnitHelper;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping(value = "/error")
public class ErrorController {

    @RequestMapping("/index")
    public ModelAndView t() {
        ModelAndView modelAndView = UnitHelper.GetCurrentUserAndView();
        modelAndView.setViewName("/error/index");
        return modelAndView;
    }

}
