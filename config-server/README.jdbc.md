Config-Server
===
```java
@EnableConfigServer
```
server:
  port: 9090
  servlet:
    context-path: /bp-config

spring:
  application:
    name: bp-config
  profiles:
    active: jdbc
  cloud:
    config:
      server:
        jdbc:
          sql: select `akey`, `avalue` from properties where application=? and profile=? and label=?
  datasource:
    url: jdbc:mysql://127.0.0.1:3306/bp_config?useUnicode=true&characterEncoding=utf8&autoReconnect=true&failOverReadOnly=false&useSSL=false
    username: root
    password: 
    driver-class-name: com.mysql.jdbc.Driver
    
Config-Client
===
#### bootstrap.yml
spring:
  application:
    name: config-client
  cloud:
    config:
      uri: http://127.0.0.1:9090/bp-config
      fail-fast: true
      label: master
      profile: dev
      name: config-client

