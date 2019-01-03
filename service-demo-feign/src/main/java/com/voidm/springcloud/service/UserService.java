package com.voidm.springcloud.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 接口绑定
 * @author voidm
 */
@FeignClient(value = "application-client-demo")
public interface UserService {

    @GetMapping("/web-client-demo/hello")
    String hello(@RequestParam("name") String name);
}