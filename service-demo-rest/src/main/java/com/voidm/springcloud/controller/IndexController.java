package com.voidm.springcloud.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

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
    private RestTemplate restTemplate;

    /**
     * 服务名称
     */
    private final String CLIENT_NAME = "application-client-demo";
    /**
     * 项目根路径名
     */
    private final String PROJECT_NAME = "web-client-demo";

    @RequestMapping({"/", "hello"})
    @ResponseBody
    public String hello(String name) {
        return "Rest Service : " + restTemplate.getForObject(String.format("http://%s/%s/hello?name=%s", CLIENT_NAME, PROJECT_NAME,name), String.class);
    }
}