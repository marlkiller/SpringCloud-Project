package com.voidm.springcloud.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
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
@RefreshScope
@RequestMapping("/")
public class IndexController {

    @Value("${config}")
    private String configValue;

    @RequestMapping({"/", "hello"})
    @ResponseBody
    public String hello() {
        return "Config : " + configValue;
    }
}