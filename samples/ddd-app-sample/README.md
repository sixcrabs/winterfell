# ddd-app-sample

DDD 模式的分层架构示例代码

## 结构
- domain: 核心层，包含 repository接口、包含业务逻辑、领域模型（实体、值对象、聚合根、领域服务、领域事件等），领域层是业务的中心， 不应包含 Spring 框架注解（如 @Service, @Autowired），保持纯净 **它不依赖任何其他层**
- infrastructure: 基础设施层，提供技术支撑，如持久化（数据库）、消息队列、外部服务调用、缓存、日志、监控、配置、工具类、第三方库集成等。 实现 Domain 层的接口（如 Repository），提供技术能力，Repository 实现类（如 @Repository，使用 Spring Data JPA/MyBatis），Adapter（适配器）。**仅依赖 domain 层**
- application: 应用层，定义和协调应用用例（Use Cases），处理事务和安全，调用领域对象完成业务。被 @Service 注解的应用服务（Application Service），接收 Command 或 Query，返回 DTO，**依赖 Domain 层**
- interface: 界面/接口层，对外暴露接口，RESTful API 端点。接收请求（HTTP/MQ/CLI），参数校验，调用 Application Service，结果转换并响应。@RestController / @Controller，接收 DTO (Request)，返回 DTO (Response) **依赖 application 层**

关键的依赖倒置原则 (DIP)
- `领域层` 定义了接口 (<AggregateName>Repository 接口)。

- `基础设施层` 实现了这些接口 (<AggregateName>RepositoryImpl)。

- `应用层` 依赖并使用 领域层 定义的接口，通过 Spring 的依赖注入（DI）机制，实际注入的是基础设施层的实现。

