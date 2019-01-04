package com.voidm.springcloud.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 配置文件获取
 *
 * @author voidm
 * @date 2019.01.02
 */
@Controller
@RefreshScope
@RequestMapping("/")
public class IndexController {

    @Value("${info.name}")
    private String infoName;
    @Value("${info.age}")
    private String infoAge;

    @RequestMapping({"/", "hello"})
    @ResponseBody
    public String hello() {
        return String.format("info.name : %s \t info.age : %s", infoName, infoAge);
    }
}