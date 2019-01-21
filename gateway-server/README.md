------------

# SpringCloud-GateWay


> 本文非常简要介绍如何使用 Spring Cloud Gateway 作为 API 网关(不是使用 zuul 作为网关)  
> 关于 Spring Cloud Gateway 和 zuul 的性能比较本文不再赘述，基本可以肯定 Spring Cloud  
> Finchley 版本的 gateway比zuul 1.x系列的性能和功能整体要好.[详情请看这里](http://www.itmuch.com/spring-cloud-sum/performance-zuul-and-gateway-linkerd/)

## GateWay-Server (服务端配置)
### pom依赖

**这里如果用starter-web启动会报错**
因为 gateway 是基于 **spring-webflux** 开发的，他依赖的 **DispatcherHandler** 就和我们 web 里的 **DispatcherServlet** 一样的功能  

```xml
<!--gateway依赖-->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-gateway</artifactId>
</dependency>
<!--注册中心依赖-->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
```

### yml配置

由于gateway用的是webflux，所以**context-path**这个设定其实是不生效的，现在还没有一个key来设定webflux的context-path  
default-filters: 里面可以定义一些共同的filter，对所有路由都起作用
routes 是个数组,可以配置多个转发规则
 - 第一个**2-application-client-demo**为转发到微服务  
 uri: lb://application-client-demo,  lb://xxx , lb 代表从注册中心获取服务,ws请求为**lb:ws://xxx** , xxx为微服务的**Application-Name**  
 lb后面的服务,必须在Eureka注册列表中,否则会报找不到的错!  
 eg: {gateway-server}/web-client-demo/hello  --> {application-client-demo}/web-client-demo/hello
 - 第二个为转发到外部域名,我这里直接转发请求到我博客.  
 StripPrefix=1表示 比如请求 **/2blog/admin **，去除掉前面一个前缀之后，最后转发到目标服务的路径为**/admin**,也可以通过 **RewritePath** 来实现
 - path为匹配的路径

```yml
eureka:
  client:
    serviceUrl:
      defaultZone: http://localhost:8080/web-eureka/eureka/
    # 但你不想整合Eureka，也可以通过下面的配置关闭
    enabled: true
server:
  port: 9098
  servlet:
    context-path: /web-server-gateway
spring:
  application:
    name: application-server-gateway
  cloud:
    gateway:
      default-filters:
      # - TokenCheck
      routes:
        # --------------------------------转发到微服务------------------------------------
        - id: 2-application-client-demo
           # uri: lb://xxx , lb 代表从注册中心获取服务,ws请求为lb:ws://xxx
          uri: lb://application-client-demo
          predicates:
            - Path=/web-client-demo/**
        # --------------------------------转发到外部域名------------------------------------
        - id: 2-voidm-blog
          uri: http://voidm.com
          predicates:
            - Path=/2blog/admin
          # 也可以通过 RewritePath 来实现
          filters:
            - StripPrefix=1
            #- RewritePath=/2blog/(?<segment>.*), /$\{segment}
      # gateway开关
      enabled: true
```

### Main启动方法
```java

/**
 * 网关启动入口
 *
 * @author voidm
 */
@SpringBootApplication
public class GatewayServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(GatewayServerApplication.class, args);
    }
}
```

### 测试请求转发

配置完成之后,就可以启动项目测试了

- 测试转发到微服务

[![](http://voidm.com/wp-content/uploads/2019/01/TIM截图20190121132350.png)](http://voidm.com/wp-content/uploads/2019/01/TIM截图20190121132350.png)

- 测试转发到外部域名

[![](http://voidm.com/wp-content/uploads/2019/01/TIM截图20190121132822.png)](http://voidm.com/wp-content/uploads/2019/01/TIM截图20190121132822.png)

### 自定义Filter过滤器

自定义一个用来检验请求是否合法的过滤器.
定义一个TokenCheckGatewayFilterFactory类实现GatewayFilterFactory接口
类名一定要为filterName + GatewayFilterFactory，如这里定 TokenCheckGatewayFilterFactory的话，它的filterName就是TokenCheck

- 代码实现

> 这里为了简单,直接验证请求头中,Authorization是否为voidM

```java
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
```

- 配置文件添加过滤  
default-filters 全局过滤中添加 TokenCheck , 也可以在routes中的filters中针对个别添加
```yml
      default-filters:
        - TokenCheck
```

### 测试CustomeFilter

[![](http://voidm.com/wp-content/uploads/2019/01/TIM截图20190121133725.png)](http://voidm.com/wp-content/uploads/2019/01/TIM截图20190121133725.png)

[![](http://voidm.com/wp-content/uploads/2019/01/TIM截图20190121133802.png)](http://voidm.com/wp-content/uploads/2019/01/TIM截图20190121133802.png)

测试成功!!!
