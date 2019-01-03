package com.voidm.springcloud;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * 服务注册中心
 * EnableDiscoveryClient 标识发现服务
 *
 * @author voidm
 */
@SpringBootApplication
@EnableDiscoveryClient
public class ServiceDemoRestApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceDemoRestApplication.class, args);
    }

    @Bean
    @LoadBalanced
    public RestTemplate getRestTemplate (){
        return new RestTemplate();
    }
}
