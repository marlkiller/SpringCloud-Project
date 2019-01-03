package com.voidm.springcloud;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 服务注册中心
 * EnableDiscoveryClient 标识发现服务
 *
 * @author voidm
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class ServiceDemoFeignApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceDemoFeignApplication.class, args);
    }
}
