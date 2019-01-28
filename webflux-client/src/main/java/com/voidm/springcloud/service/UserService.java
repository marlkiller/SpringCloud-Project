package com.voidm.springcloud.service;

import com.voidm.springcloud.doc.User;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * @author voidm
 */
@Service
public class UserService {

    private List<User> users = new ArrayList<>(10);

    @PostConstruct
    public void init() {
        users.add(new User(1, "admin"));
        users.add(new User(2, "admin2"));
        users.add(new User(3, "admin3"));
    }

    public Flux<User> getAllUser() {
        return Flux.fromIterable(users);
    }

    public Mono<User> getUserById(int id) {
        return Mono.just(users.get(0));
    }

    public Mono<Void> saveUser(Mono<User> userMono) {
        Mono<User> mono = userMono.doOnNext(user ->
                users.add(user)
        );
        return mono.then();
    }
}