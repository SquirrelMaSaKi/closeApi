- Eureka是注册中心，收集所有的服务，必须以server身份开启
- Redis缓存必须以client身份注册到Eureka，其实也是一种服务
- zuul属于网关服务，也是一种服务，以client身份注册到Eureka中，可以设置动态路由