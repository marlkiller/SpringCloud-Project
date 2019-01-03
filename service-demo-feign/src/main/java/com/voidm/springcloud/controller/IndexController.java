package com.voidm.springcloud.controller;

import com.voidm.springcloud.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 服务调用方
 *
 * @author voidm
 * @date 2019.01.02
 */
@Controller
@RequestMapping("/")
public class IndexController {

    @Autowired
    private UserService userService;

    @RequestMapping({"/", "hello"})
    @ResponseBody
    public String hello(String name) {
        return "Feign Service : " + userService.hello(name);
    }
}