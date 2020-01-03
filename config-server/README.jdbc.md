Config-Server
===
```java
@EnableConfigServer
```
```yml
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
```
    
Config-Client
===
#### bootstrap.yml
```yml
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
```


SQL
===
```sql
CREATE TABLE `properties` (
`id` INT ( 11 ) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键--自增长',
`akey` VARCHAR ( 255 ) COLLATE utf8mb4_unicode_ci NOT NULL,
`avalue` VARCHAR ( 1000 ) COLLATE utf8mb4_unicode_ci NOT NULL,
`application` VARCHAR ( 50 ) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '应用名称',
`profile` VARCHAR ( 50 ) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '应用模块',
`label` VARCHAR ( 50 ) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '应用环境',
PRIMARY KEY ( `id` ) 
) ENGINE = INNODB AUTO_INCREMENT = 1050 DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

INSERT INTO `bp_config`.`properties`(`id`, `akey`, `avalue`, `application`, `profile`, `label`) VALUES (1049, 'foo', 'foo', 'config-client', 'dev', 'master');
```
