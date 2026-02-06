## misc

开发中常用的一些组件封装

- event-manager: 事件管理模块，基于 disruptor 实现高性能的异步事件管理
- ring-timer: 定时器模块，基于时间轮算法实现，可用于进行轮询任务等
- hutool-mini: 一个基于`hutool`的`mini`版本(仅封装一些常用功能)
- zinc-client: 一个操作`zinc-search`的`client`
- srpc: 一个轻量级的`rpc`库
- mrc: 兼容微服务的 `rest` 客户端
- oss-client: 对象存储操作客户端
- key-store: 一个可以基于多种存储介质的kv模块，可用于临时的数据存储、应用缓存等场景，支持 内存/mapdb/redis/mvstore
- indigo: office文档生成操作库