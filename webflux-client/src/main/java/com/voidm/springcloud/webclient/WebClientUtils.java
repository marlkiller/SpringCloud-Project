package com.voidm.springcloud.webclient;

import com.voidm.springcloud.doc.User;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

/**
 * WebClient 是一个非阻塞、响应式的HTTP客户端，它以响应式被压流的方式执行HTTP请求
 * WebClient默认使用 Reactor Netty 作为HTTP连接器，当然也可以通过ClientHttpConnector修改其它的HTTP连接器。
 *
 * @author voidm
 * @date 2019/1/25
 */
public class WebClientUtils {

    public static void main(String[] args) {

        // 如果您只使用特定服务的API，那么您可以使用该服务的baseUrl来初始化WebClient
        WebClient webClient = WebClient.create("http://localhost:8083/");
        // 该retrieve()方法是获取响应主体的最简单方法
        // 如果您希望对响应拥有更多的控制权，那么您可以使用可exchange()访问整个ClientResponse标题和正文的方法 -
        WebClient.ResponseSpec retrieve = webClient.get().uri("/handler/users").retrieve();
        // .uri("/user/repos?sort={sortField}&direction={sortDirection}","updated", "desc") // 参数都被花括号包围。在提出请求之前，这些参数将被WebClient自动替换 -


        // bodyToFlux 集合 bodyToMono 为对象
        List<User> block = retrieve.bodyToFlux(User.class).collectList().block();
        System.out.println(block);

        // 自定义类型
        ParameterizedTypeReference<Map<Integer, User>> typeReference = new ParameterizedTypeReference<Map<Integer, User>>() {};
        retrieve = webClient.get().uri("/handler/typeReference").retrieve();
        Mono<Map<Integer, User>> mapMono = retrieve.bodyToMono(typeReference);
        Map<Integer, User> map = mapMono.block();
        System.out.println(map);
    }

}