package com.voidm.springcloud.controller;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

/**
 * @author voidm
 */
@Configuration
public class RoutingConfiguration {

    @Bean
    public RouterFunction<ServerResponse> monoRouterFunction(UserHandler userHandler) {

        return route(GET("/handler/users").and(accept(MediaType.APPLICATION_JSON)), userHandler::getAllUser)
                .andRoute(GET("/handler/user/{id}").and(accept(MediaType.APPLICATION_JSON)), userHandler::getUserById)
                .andRoute(POST("/handler/save").and(accept(MediaType.APPLICATION_JSON)), userHandler::saveUser)
                .andRoute(GET("/handler/typeReference").and(accept(MediaType.APPLICATION_JSON)), userHandler::typeReference);
    }

}