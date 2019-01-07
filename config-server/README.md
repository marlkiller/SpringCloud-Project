------------

# SpringCloud-Config

## Config-Server (服务端配置)
### yml配置

- 远程配置文件相关

uri: Git 地址
search-paths: 是搜索路径,只要是**父路径**即可
search-paths 路径之内 添加一个或多个 Config配置文件 按照 {**Application-name**}-{**profile**}.yml 格式命名

> 这里配置文件一定要注意格式,否则取的时候会报错
> 推荐使用properties配置文件, 因为yml对格式要求比较高,这了坑我踩了好久 ...如下图
[![](http://voidm.com/wp-content/uploads/2019/01/TIM截图20190104140808.png)](http://voidm.com/wp-content/uploads/2019/01/TIM截图20190104140808.png)

为了方便管理 以及后面的远程配置自动更新 ,强烈建议配置文件放在额外的一个仓库里!
Github-Config-Repo : [https://github.com/marlkiller/SpringCloud-Config-Repo](https://github.com/marlkiller/SpringCloud-Config-Repo)

- Config-Client-yml配置项

```yml
server:
  port: 9090
  servlet:
    # Web根路径
    context-path: /web-config-server
spring:
  application:
    # 应用名称
    name: application-config-server
  cloud:
    config:
      server:
        git:
          uri: https://github.com/marlkiller/SpringCloud-Config-Repo
```



------------


### Pom依赖

这块不需要start.web,只需要一个config-server

```xml
    <dependencies>
        <!--config-server依赖-->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-config-server</artifactId>
        </dependency>
    </dependencies>
```

------------


### Main方法
**开始EnableConfigServer注解**
```java
/**
 * Config 配置服务
 *
 * @author voidm
 * @date 2019.01.02
 */
@EnableConfigServer
@SpringBootApplication
public class ConfigServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConfigServerApplication.class, args);
    }
}

```

项目启动后, 访问如下地址:
请求格式 为 : http://{Host:Port}/{Content-Path}/{Application-name}/{profile}
或者 : http://{Host:Port}/{Content-Path}/{Application-name}-{profile}.yml
后缀 可以是 .yml .properties ,  Spring会自动转换格式!

> 这里配置文件一定要注意格式,否则取的时候会报错
> 推荐使用properties配置文件, 因为yml对格式要求比较高,这了坑我踩了好久 ...

如下图 :
[![](http://voidm.com/wp-content/uploads/2019/01/TIM截图20190104141055.png)](http://voidm.com/wp-content/uploads/2019/01/TIM截图20190104141055.png)


------------


## Config-Client (客户端配置)
### yml配置
config.uri是**Config-Server**的地址
profile是对应配置文件的**profile**
label 是 git分支, 默认是**master**

- bootstrap.yml
```yml
spring:
  application:
    name: application-config-server
  cloud:
    config:
      uri: http://localhost:9090/web-config-server/
      profile: dev
      label: master
```
- application.yml

```yml
server:
  port: 9091
  servlet:
    context-path: /web-config-client
```

> 这里要注意,yml配置文件有俩个
> Git相关参数,还有Config对应的AppName\Profile要配置到bootstrap.yml, 否则启动会报错


### Pom依赖
```xml
<!--Spring Cloud Config 客户端依赖-->
<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-starter-config</artifactId>
</dependency>
```


### Main方法

提供一个Api接口获取读取到的Config
```java
/**
 * 配置文件获取
 *
 * @author voidm
 * @date 2019.01.02
 */
@Controller
@RefreshScope
@RequestMapping("/")
public class IndexController {
    @Value("${info.name}")
    private String infoName;
    @Value("${info.age}")
    private String infoAge;

    @RequestMapping({"/", "hello"})
    @ResponseBody
    public String hello() {
        return String.format("info.name : %s \t info.age : %s", infoName, infoAge);
    }
}
```

```java
@SpringBootApplication
public class ConfigClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConfigClientApplication.class, args);
    }
}
```

启动项目 , 测试,如下图 (配置获取成功)

[![](http://voidm.com/wp-content/uploads/2019/01/TIM截图20190104141833.png)](http://voidm.com/wp-content/uploads/2019/01/TIM截图20190104141833.png)


## 自动刷新配置
此时,如果手动update Git上的配置时, 我们Client里的配置是不会刷新的,
这个配置只有在项目初始化的时候会拉取一次,怎么办呢?

- 首先在Client 添加Pom依赖,开启监控

```xml
        <!-- 刷新配置 -->
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

配置Actuator开放页面节点  默认只开启了health、info两个节点,可以手动配置节点,
用,分割,eg:beans,info,health
如果是开发所有节点, 可以用* , 在yml配置文件中为 include: "*"  
注意引号~!!
- 然后在application.yml配置文件中添加过滤节点
```yml
management:
  endpoints:
    web:
      exposure:
        include: "*"
```
- 之后在有配置文件注解@value的类上添加RefreshScope注解

> API根路径路径默认是actuator!也可以手动配置
> Refresh-API : Host:Port/actuator/refresh

此时的配置项是半自动的,Git服务器中配置文件更改后,
Client发送一个POST请求,可以来手动刷新配置项(**如果上面的不配置,该Api会404**)

[![](http://voidm.com/wp-content/uploads/2019/01/TIM截图20190104161514.png)](http://voidm.com/wp-content/uploads/2019/01/TIM截图20190104161514.png)

Refresh 之后, Client中的配置则为最新, 如果要做到实时更新的话,
需要在git仓库Settings中添加一个WebHook,就是在git每次提交代码后,
自动发送一个refresh请求到你的ClientServer,
这里由于我项目在本地搭建,外网不能访问,So,就不测试了...

[![](http://voidm.com/wp-content/uploads/2019/01/TIM截图20190104162531-1024x466.png)](http://voidm.com/wp-content/uploads/2019/01/TIM截图20190104162531.png)
