这里使用了redis-cache缓存
并作为一个服务配置到Eureka服务上
这样可以让多个其他客户端调用该服务，当缓存配置需要更改的时候，我们只需要更改Eureka上的redis缓存配置即可


