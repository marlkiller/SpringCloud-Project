package com.voidm.springcloud;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * 内部调用服务
 *
 * @author voidm
 * @date 2019.01.02
 */
@SpringBootApplication
@EnableEurekaClient
public class ClientDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClientDemoApplication.class, args);
    }
}
