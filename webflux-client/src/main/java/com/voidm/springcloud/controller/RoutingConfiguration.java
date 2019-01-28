package com.voidm.springcloud.controller;

import com.voidm.springcloud.doc.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

/**
 * @author voidm
 */
@Configuration
public class RoutingConfiguration {

    @Bean
    public RouterFunction<ServerResponse> monoRouterFunction(UserHandler userHandler) {


        // lambda事件绑定
        return route(GET("/handler/users").and(accept(MediaType.APPLICATION_JSON)), userHandler::getAllUser)
                .andRoute(GET("/handler/user/{id}").and(accept(MediaType.APPLICATION_JSON)), userHandler::getUserById)
                .andRoute(POST("/handler/save").and(accept(MediaType.APPLICATION_JSON)), userHandler::saveUser)
                // .andRoute(GET("/handler/typeReference").and(accept(MediaType.APPLICATION_JSON)), userHandler::typeReference)
                .andRoute(GET("/handler/typeReference").and(accept(MediaType.APPLICATION_JSON)), serverRequest -> {
                    Map<Integer, Object> map = new HashMap<>(1);
                    map.put(1, new User(1, "void"));
                    return ServerResponse.ok().body(Mono.just(map), Map.class);
                });
    }

}