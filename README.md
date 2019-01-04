> 开发环境 :
> JDK 1.8 / IDEA / MAVEN 3 /SPRINGBOOT 2.0.2 RELEASE

------------

- [SpringCloud-Config 配置中心服务器与客户端的搭建使用](https://github.com/marlkiller/SpringCloud-Project/blob/master/config-server/README.md "配置中心服务器与客户端的搭建使用")

# SpringCloud-Parent

SpringBoot Version : 2.0.2.RELEASE
SpringCloud Version : Finchley.BUILD-SNAPSHOT

首先创建一个聚合工程,SpringBootDemo-Parent
```xml
    <!-- SpringBoot Parent -->
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.0.2.RELEASE</version>
        <relativePath/>
    </parent>

    <!-- Spring Cloud 依赖-->
    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>${spring-cloud.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>

    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
        <java.version>1.8</java.version>
        <spring-cloud.version>Finchley.BUILD-SNAPSHOT</spring-cloud.version>
    </properties>

```

聚合工程包含四个Module,
分别是eureka注册中心服务器,内部调用服务,还有对外暴漏服务(又分为**rest**调用 跟 **feign** 调用俩种版本)
```xml
    <modules>
        <module>eureka-server</module>
        <module>client-demo</module>
        <module>service-demo-rest</module>
        <module>service-demo-feign</module>
    </modules>
```

此外 还要额外配置**Spring官方私服**,部分Jar Maven公服会找不到,后面会用到
```xml
<repositories>
        <repository>
            <id>spring-snapshots</id>
            <name>Spring Snapshots</name>
            <url>https://repo.spring.io/snapshot</url>
            <snapshots>
                <enabled>true</enabled>
            </snapshots>
        </repository>
        <repository>
            <id>spring-milestones</id>
            <name>Spring Milestones</name>
            <url>https://repo.spring.io/milestone</url>
            <snapshots>
                <enabled>false</enabled>
            </snapshots>
        </repository>
    </repositories>
```
项目结构图,如下

[![](http://voidm.com/wp-content/uploads/2019/01/TIM截图20190103112051.png)](http://voidm.com/wp-content/uploads/2019/01/TIM截图20190103112051.png)

------------
## Module-Eureka (注册中心)

Web : http://127.0.0.1:8080/web-eureka/
Spring-Application-Name : application-eureka
defaultZone : http://127.0.0.1:8080/web-eureka//eureka/

yml配置文件
```yml
server:
  port: 8080
  servlet:
    # Web根路径
    context-path: /web-eureka
eureka:
  client:
    registerWithEureka: false
    fetchRegistry: false
    serviceUrl:
      defaultZone: http://localhost:${server.port}/web-eureka/eureka/

  server:
    waitTimeInMsWhenSyncEmpty: 0
spring:
  application:
    # 应用名称
    name: application-eureka

```

Pom依赖
```xml
    <dependencies>
        <!-- Eureka服务端依赖 -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
    </dependencies>
```
Main方法添加**EnableEurekaServer**注解

```java
/**
 * 服务注册中心
 *
 * @author voidm
 * @date 2019.01.02
 */
@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(EurekaServerApplication.class, args);
    }
}

```

yml配置文件
四个web模块 , content-path,都以**/web** 开头 , 注意 一定要加/ 否则启动会报错!
application-name 以 application开头,SpringCLoud的Rpc调用是以Application-Name发现并找到相应的服务,所以这个很重要

```yml
server:
  port: 8080
  servlet:
    # Web根路径
    context-path: /web-eureka
eureka:
  client:
    registerWithEureka: false
    fetchRegistry: false
    serviceUrl:
      defaultZone: http://localhost:${server.port}/web-eureka/eureka/

  server:
    waitTimeInMsWhenSyncEmpty: 0
spring:
  application:
    # 应用名称
    name: application-eureka
```

------------
## Module-Client

Web : http://127.0.0.1:8081/web-client-demo/
Spring-Application-Name : application-client-demo

yml配置文件
```yml
eureka:
  client:
    serviceUrl:
      defaultZone: http://localhost:8080/web-eureka/eureka/
server:
  port: 8081
  servlet:
    context-path: /web-client-demo
spring:
  application:
    name: application-client-demo
```

Pom依赖
```xml
        <!--注册中心依赖-->
<dependency>
      <groupId>org.springframework.cloud</groupId>
      <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
</dependency>
```

提供一个对内接口,供service调用
```java
/**
 * 对内暴漏接口
 *
 * @author voidm
 * @date 2019.01.02
 */
@Controller
@RequestMapping("/")
public class IndexController {

    @RequestMapping({"/", "hello"})
    @ResponseBody
    public String hello(String name) {
        return "Client Response : Params > name = " + name;
    }
}
```

启动Main方法,启动EnableEurekaClient注解
```java
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

```
------------
## Module-Service
众所周知,SpringCloud的是基于HTTP协议的RPC调用,相对于dubbo的TCP协议要慢,而且流量要多
但是也相对灵活,HTTP调用也要简单一些
对于Java来讲,Spring实现了俩种调用实现,RestTempment/Feign, Feign要更优雅一些,
俩种实现实际上也是对HTTP的不用封装.

### Service-Rest

Web : http://127.0.0.1:8082/web-service-demo-rest/
Spring-Application-Name : application-service-demo-rest

yml配置文件
```yml
eureka:
  client:
    serviceUrl:
      defaultZone: http://localhost:8080/web-eureka/eureka/
server:
  port: 8082
  servlet:
    context-path: /web-service-demo-rest
spring:
  application:
    name: application-service-demo-rest
```

Pom依赖
```xml
<dependencies>
    <!--注册中心依赖-->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
    </dependency>
    <!--Ribbon负载均衡依赖-->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-ribbon</artifactId>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
</dependencies>
```
提供一个对外暴漏的接口,对接Client采用**RestTemplate**
```java
/**
 * 服务调用方
 *
 * @author voidm
 * @date 2019.01.02
 */
@Controller
@RequestMapping("/")
public class IndexController {
    @Autowired
    private RestTemplate restTemplate;

    /**
     * 服务名称
     */
    private final String CLIENT_NAME = "application-client-demo";
    /**
     * 项目根路径名
     */
    private final String PROJECT_NAME = "web-client-demo";

    @RequestMapping({"/", "hello"})
    @ResponseBody
    public String hello(String name) {
        return "Rest Service : " + restTemplate.getForObject(String.format("http://%s/%s/hello?name=%s", CLIENT_NAME, PROJECT_NAME,name), String.class);
    }
}
```
启动Main方法,开启EnableDiscoveryClient注解,发现Client服务
实例化RestTemplate Bean,这里一定要加上**LoadBalanced**注解
否则不会根据ApplicationName去表示发现服务,直接404
```java
/**
 * 服务注册中心
 * EnableDiscoveryClient 标识发现服务
 *
 * @author voidm
 */
@SpringBootApplication
@EnableDiscoveryClient
public class ServiceDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceDemoApplication.class, args);
    }

    @Bean
    @LoadBalanced
    public RestTemplate getRestTemplate (){
        return new RestTemplate();
    }
}

```

------------
### Service-Feign

Web : http://127.0.0.1:8084/web-service-demo-feign/
Spring-Application-Name : application-service-demo-feign

------------
yml配置文件
```yml
eureka:
  client:
    serviceUrl:
      defaultZone: http://localhost:8080/web-eureka/eureka/
server:
  port: 8084
  servlet:
    context-path: /web-service-demo-feign
spring:
  application:
    name: application-service-demo-feign
```


Pom依赖

feign依赖,这块有个坑
由于SpringCLoud各组件版本迭代更新,依赖jar目录迁移,
导致Feign依赖包Miss,找不到,这里找到了一种解决方案
**2.0.0.RC1** 这个版本的jar,是在之前配置的**Spring私服**找到的,
具体详细情况,请点[这里](https://blog.csdn.net/alinyua/article/details/80070890 "这里")
```xml
<dependencies>
    <!--注册中心依赖-->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
    </dependency>
    <!--Ribbon负载均衡依赖-->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-ribbon</artifactId>
    </dependency>

    <!--openfeign 依赖-->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-openfeign</artifactId>
    </dependency>

    <!--openfeign 依赖-->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-openfeign</artifactId>
        <version>2.0.0.RC1</version>
        <type>pom</type>
        <scope>import</scope>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
</dependencies>
```

FeignClient.value 是 Client的Application-name
GetMapping.value 是 项目名称(content-path) + Api地址
```java
/**
 * 接口绑定
 * @author voidm
 */
@FeignClient(value = "application-client-demo")
public interface UserService {

    @GetMapping("/web-client-demo/hello")
    String hello(@RequestParam("name") String name);
}
```
```java
/**
 * 服务调用方
 *
 * @author voidm
 * @date 2019.01.02
 */
@Controller
@RequestMapping("/")
public class IndexController {

    @Autowired
    private UserService userService;

    @RequestMapping({"/", "hello"})
    @ResponseBody
    public String hello(String name) {
        return "Feign Service : " + userService.hello(name);
    }
}
```
Main启动方法,除了EnableDiscoveryClient发现服务外,还需要开启Feign注解

```java
/**
 * 服务注册中心
 * EnableDiscoveryClient 标识发现服务
 *
 * @author voidm
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class ServiceDemoFeignApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceDemoFeignApplication.class, args);
    }
}
```

------------

## 测试
按照如下顺序,依次启动四个web-module
[![](http://voidm.com/wp-content/uploads/2019/01/TIM截图20190103112252.png)](http://voidm.com/wp-content/uploads/2019/01/TIM截图20190103112252.png)

启动完毕后,访问注册中(**不包括注册中心本身,所以发现三个服务**)

[![](http://voidm.com/wp-content/uploads/2019/01/TIM截图20190103112449.png)](http://voidm.com/wp-content/uploads/2019/01/TIM截图20190103112449.png)

------------


最后,依次访问俩个对外暴漏接口

[![](http://voidm.com/wp-content/uploads/2019/01/TIM截图20190103112701.png)](http://voidm.com/wp-content/uploads/2019/01/TIM截图20190103112701.png) [![](http://voidm.com/wp-content/uploads/2019/01/TIM截图20190103112706.png)](http://voidm.com/wp-content/uploads/2019/01/TIM截图20190103112706.png)

**Successful, 测试成功 !**

