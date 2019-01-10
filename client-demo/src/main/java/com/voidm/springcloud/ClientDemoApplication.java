package com.voidm.springcloud;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 内部调用服务
 * 新版本默认包含EnableDiscoveryClient注解,可以不写
 * 如果选用的注册中心是eureka，那么就推荐@EnableEurekaClient
 * 如果是其他的注册中心，那么推荐使用@EnableDiscoveryClient。
 *
 * @author voidm
 * @date 2019.01.02
 */
@SpringBootApplication
// @EnableEurekaClient
public class ClientDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClientDemoApplication.class, args);
    }
}
