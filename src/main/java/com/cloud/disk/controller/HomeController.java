package com.cloud.disk.controller;

import com.cloud.disk.dtos.EditUserDto;
import org.apache.shiro.SecurityUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = "/home")
public class HomeController {

    @GetMapping("index")
    public ModelAndView index(){
        EditUserDto user=(EditUserDto)SecurityUtils.getSubject().getPrincipal();
        ModelAndView modelAndView=new ModelAndView("home/index");
        modelAndView.addObject("currentUserId",user.getLoginName());
        return modelAndView;
    }

    @GetMapping("main")
    public ModelAndView main()
    {
        return new ModelAndView("home/main");
    }

}
