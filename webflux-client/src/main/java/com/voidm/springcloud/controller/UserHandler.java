package com.voidm.springcloud.controller;

import com.voidm.springcloud.doc.User;
import com.voidm.springcloud.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

/**
 * 基于Handler功能
 * 需由Router事件绑定
 *
 * @author voidm
 * @date 2019/1/25
 */

@Component
public class UserHandler {

    private UserService userService;

    @Autowired
    public UserHandler(UserService userService) {
        this.userService = userService;
    }

    Mono<ServerResponse> getAllUser(ServerRequest serverRequest) {
        Flux<User> fluxUsers = userService.getAllUser();
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(fluxUsers, User.class);
    }

    Mono<ServerResponse> getUserById(ServerRequest serverRequest) {
        int uid = Integer.valueOf(serverRequest.pathVariable("id"));
        Mono<User> monoUser = userService.getUserById(uid);
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(monoUser, User.class);
    }

    Mono<ServerResponse> saveUser(ServerRequest serverRequest) {
        Mono<User> user = serverRequest.bodyToMono(User.class);
        return ServerResponse.ok().build(userService.saveUser(user));
    }

    Mono<ServerResponse> typeReference(ServerRequest serverRequest) {
        Map<Integer, Object> map = new HashMap<>();
        map.put(1, new User(1, "void"));
        return ServerResponse.ok().body(Mono.just(map), Map.class);
    }
}