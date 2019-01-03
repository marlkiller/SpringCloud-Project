package com.voidm.springcloud.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 对内暴漏接口
 *
 * @author voidm
 * @date 2019.01.02
 */
@Controller
@RequestMapping("/")
public class IndexController {

    @RequestMapping({"/", "hello"})
    @ResponseBody
    public String hello(String name) {
        return "Client Response : Params > name = " + name;
    }
}