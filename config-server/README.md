------------

# SpringCloud-Config

## Config-Server (服务端配置)
### yml配置
Git相关参数配置

uri: Git 地址
search-paths: 是搜索路径,只要是**父路径**即可
search-paths 路径之内 添加一个或多个 Config配置文件 按照 {**Application-name**}-{**profile**}.yml 格式命名

[![](http://voidm.com/wp-content/uploads/2019/01/TIM截图20190104140808.png)](http://voidm.com/wp-content/uploads/2019/01/TIM截图20190104140808.png)

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

```yml
server:
  port: 9091
  servlet:
    context-path: /web-config-client
spring:
  application:
    name: application-config-server
  cloud:
    config:
      uri: http://localhost:9090/web-config-server/
      profile: pro
      label: master

info:
  age: 0
  name: default
```

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
