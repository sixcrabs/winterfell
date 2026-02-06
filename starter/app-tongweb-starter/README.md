## tong-web-app-starter

适用于 东方通企业版的 web starter

### 使用
1. 引入pom

```xml
<denpendency>
    <groupId>cn.piesat.v</groupId>
    <artifactId>app-starter-tongweb</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</denpendency>
```
2. 修改入口类

    改造入口类，继承 `SpringBootServletInitializer`
```java
public class CloudSampleApplication extends SpringBootServletInitializer {

    /**
     *  这里表示使用外部的tomcat容器
     * @param builder
     * @return
     */
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        // 注意这里要指向原先用main方法执行的启动类
        return builder.sources(CloudSampleApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(CloudSampleApplication.class, args);
    }
}
```
3. 修改打包方式
 
    采用 springboot maven plugin 打包为 war 包

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">

    <modelVersion>4.0.0</modelVersion>

    <artifactId>tong-web-app-starter</artifactId>
    <description>TongWeb App Starter</description>
   
    <packaging>war</packaging>
     <build>
         <plugins>
             <plugin>
                 <groupId>org.apache.maven.plugins</groupId>
                 <artifactId>maven-war-plugin</artifactId>
                 <version>3.0.0</version>
                 <configuration>
                     <failOnMissingWebXml>false</failOnMissingWebXml>
                 </configuration>
             </plugin>
             <plugin>
                 <groupId>org.springframework.boot</groupId>
                 <artifactId>spring-boot-maven-plugin</artifactId>
             </plugin>
         </plugins>
     </build>
</project>
```

4. 东方通控制台部署应用

打开 console： `http://localhost:9060/console`

部署：
![部署图](https://nync.piesat.cn/oss/images/tongweb_deploy.png)