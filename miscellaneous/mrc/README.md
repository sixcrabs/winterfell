## Mrc

> Micro Rest Client

兼容微服务的 `rest` 客户端

### 特性

- 支持自动服务发现（基于 `nacos`）
- 支持拦截器、重试、熔断等特性

### 用法
---

1. 引入依赖
```xml
        <dependency>
            <groupId>cn.piesat.v</groupId>
            <artifactId>mrc</artifactId>
            <version>1.1.2-SNAPSHOT</version>
        </dependency>
```

2. 添加注解
```java
@EnableMrClients(basePackages = {"cn.piesat.nj.samples.clients"})
public class SampleApp {

    public static void main(String[] args) {
        SpringApplication.run(SampleApp.class, args);
    }
}
```
> 包名根据实际项目路径

3. 编写调用接口
```java
@MrClient(name = "github-service", url = "${github.url}")
public interface GitHubClient {

    @GET("/users/{username}")
    Map getUser(@Path("username") String username);
}
```

> 注1: ` @GET("/users/{username}")` 中 `/` 开头表示从根路径开始,否则表示从当前路径开始

> 注2： 自动服务发现基于 `nacos`,根据配置的`name`属性去注册中心中找到对应的可用服务地址，此时会忽略 `url` 的配置信息

> 注3： 支持 `https` 协议的接口


### 定制 builder 和 全局interceptor

```java
@Configuration
@AutoConfigureBefore(MrClientAutoConfiguration.class)
public class MrClientConfig implements MrClientInterceptorConfigurer,
        MrClientConfigBuilderCustomizer {

    private static final Logger logger = LoggerFactory.getLogger(MrClientConfig.class);

    @Override
    public void customize(OkHttpClient.Builder builder) {
        logger.info("[mr-client] customize builder...");
        // 设置连接超时等参数
        builder.connectTimeout(3, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .followRedirects(true)
                .connectionPool(new ConnectionPool(20,
                        10, TimeUnit.SECONDS));
    }

    @Override
    public Interceptor config() {
        return HeaderUserInfoInterceptor.INSTANCE;
    }
}
```

### 定制client级别的interceptor

拦截器实现类:
```java
public class TodosClientInterceptor implements MrcInterceptor {
    @Override
    public boolean shouldSkip() {
        return false;
    }

    @Override
    public Interceptor nativeInterceptor() {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                System.out.println("我是client级别的拦截器！");
                return chain.proceed(chain.request());
            }
        };
    }
}
```
添加注解:
```java
@MrClient(name = "todo-client", url = "https://jsonplaceholder.typicode.com/",
        interceptor = TodosClientInterceptor.class)
public interface TodosClient {

    @GET("todos/{id}")
     Map todos(@Path("id") String id);
}
```


