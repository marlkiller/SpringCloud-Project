package com.voidm.springcloud.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * 自定义过滤器
 *
 * 类名一定要为filterName + GatewayFilterFactory
 * 如这里定义为JwtCheckGatewayFilterFactory的话，它的filterName就是JwtCheck
 *
 * @author voidm
 */
@Component
public class TokenCheckGatewayFilterFactory extends AbstractGatewayFilterFactory<Object> {

    @Override
    public GatewayFilter apply(Object config) {
        return (exchange, chain) -> {
            String token = exchange.getRequest().getHeaders().getFirst("Authorization");
            //校验Token的合法性
            if ("voidM".equals(token)) {
                return chain.filter(exchange);
            }
            //不合法
            ServerHttpResponse response = exchange.getResponse();
            //设置headers
            HttpHeaders httpHeaders = response.getHeaders();
            httpHeaders.add("Content-Type", "application/json; charset=UTF-8");
            httpHeaders.add("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");

            DataBuffer bodyDataBuffer = response.bufferFactory().wrap("token 不合法".getBytes());
            return response.writeWith(Mono.just(bodyDataBuffer));
        };
    }
}