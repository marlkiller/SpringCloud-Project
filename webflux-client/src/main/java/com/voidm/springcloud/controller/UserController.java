package com.voidm.springcloud.controller;

import com.voidm.springcloud.doc.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;

/**
 * 基于SpringMvc注解
 *
 * @author voidm
 * @date 2019.01.02
 */
@RestController
@RequestMapping("/")
public class UserController {

    @RequestMapping({"/", "hello"})
    public String hello(String name) {
        return "123";
    }

    @GetMapping("/{id}")
    public Mono<User> getUserById(@PathVariable("id") Long id) {
        return Mono.just(new User(Math.toIntExact(id),"void"));
    }

    @GetMapping("/getAll")
    public Flux<User> getAllUser() {
        return Flux.fromIterable(Collections.singleton(new User(1,"voidm")));
    }

    @RequestMapping("web-flux-client")
    public Mono<ResponseEntity> webFluxClient(String name) {
        // CREATED(201, "Created"),
        return Mono.just(new ResponseEntity<>("WebFlux Client Response : Params > name = " + name, HttpStatus.CREATED));
    }
}

