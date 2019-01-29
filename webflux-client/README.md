> Spring WebFlux 是随 Spring 5 推出的响应式 Web 框架.
> 其中, **Spring GateWay** 也是基于 WebFlux 实现

> Spring Boot Webflux 就是基于 Reactor 实现的。Spring Boot 2.0 包括一个新的 spring-webflux 模块。该模块包含对响应式 HTTP 和 WebSocket 客户端的支持，以及对 REST，HTML 和 WebSocket 交互等程序的支持。一般来说，Spring MVC 用于同步处理，Spring Webflux 用于异步处理。

## Pom依赖

这里不是MVC常用的 starter-web ,而是 starter-webflux

```xml
<!--webflux依赖-->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webflux</artifactId>
</dependency>
```

## Yml配置项

 **context-path** 这个设定其实是不生效的  
 WebFlux 因为没有 DispatchServlet , 已经不支持 ContextPath 了
 现在还没有一个 key 来设定 webflux 的 **context-path**

```yml
server:
  port: 8083
  servlet:
    context-path: /web-flux-client
``` 

## Api编程模型

WebFlux 提供了 WebHandler API 去定义非阻塞 API 抽象接口。可以选择以下两种编程模型实现

### 1. 注解控制层
> 和 MVC 保持一致，WebFlux 也支持响应性 @RequestBody 注解。

```java
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

```


### 2. 功能性端点
> 基于 lambda 轻量级编程模型，用来路由和处理请求的小工具。和上面最大的区别就是，这种模型，全程控制了请求 - 响应的生命流程

#### 处理器Handler

```java
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
        Flux<User> allUser = userService.getAllUser();
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(allUser, User.class);
    }

    Mono<ServerResponse> getUserById(ServerRequest serverRequest) {
        int uid = Integer.valueOf(serverRequest.pathVariable("id"));
        Mono<User> user = userService.getUserById(uid);
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(user, User.class);
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
```

#### 路由器Router

Handler 需要由 Router ( Lambda 语法) 事件绑定

```java
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
```


## 响应式编程
响应式项目编程实战中，通过基于 Reactive Streams 规范实现的框架 Reactor 去实战。Reactor 一般提供两种响应式 API :  

- Mono：实现发布者，并返回 0 或 1 个元素
- Flux：实现发布者，并返回 N 个元素

## WebClient

除了服务器端实现之外，WebFlux 也提供了反应式客户端，可以访问 HTTP、SSE 和 WebSocket 服务器端。

- 响应为集合 bodyToFlux
- 响应为单个对象 bodyToMono
- 响应为自定义类型 bodyToMono(new ParameterizedTypeReference<Map<Integer, User>>() {})

```java

public class WebClientUtils {

    public static void main(String[] args) {

        // 如果您只使用特定服务的API，那么您可以使用该服务的baseUrl来初始化WebClient
        WebClient webClient = WebClient.create("http://localhost:8083/");
        // 该retrieve()方法是获取响应主体的最简单方法
        // 如果您希望对响应拥有更多的控制权，那么您可以使用可exchange()访问整个ClientResponse标题和正文的方法 -
        WebClient.ResponseSpec retrieve = webClient.get().uri("/handler/users").retrieve();
        // .uri("/user/repos?sort={sortField}&direction={sortDirection}","updated", "desc") // 参数都被花括号包围。在提出请求之前，这些参数将被WebClient自动替换 -


        // 返回集合用 bodyToFlux 返回单个对象用 bodyToMono
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
```

## 跨域问题解决

不同意传统 MVC 依赖 Servlet Filter
ServerHttpRequest 是 org.springframework.http.server.reactive 下的类
而不是 Servlet-Api 包下的

```java
@Bean
public WebFilter corsFilter() {
    return (ServerWebExchange ctx, WebFilterChain chain) -> {
        ServerHttpRequest request = ctx.getRequest();
        if (CorsUtils.isCorsRequest(request)) {
            ServerHttpResponse response = ctx.getResponse();
            HttpHeaders headers = response.getHeaders();
            headers.add("Access-Control-Allow-Origin", "*");
            headers.add("Access-Control-Allow-Methods", "*");
            headers.add("Access-Control-Max-Age", "18000L");
            headers.add("Access-Control-Allow-Headers", "*");
            headers.add("Access-Control-Expose-Headers", "*");
            headers.add("Access-Control-Allow-Credentials", "true");
            if (request.getMethod() == HttpMethod.OPTIONS) {
                response.setStatusCode(HttpStatus.OK);
                return Mono.empty();
            }
        }
        return chain.filter(ctx);
    };
}
```