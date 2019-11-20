# 动态路由
> 不需要每次手动更改我们的源代码,可以动态的获取到服务列表,然后动态调用的方式
> 比如，如果不使用动态路由，我们虽然可以通过http://localhost:11000/actuator/routes
> 获取到所有的服务名称,但是我们不知道具体的方法路径，还需要去拼接
> 所以，我们完全可以在缓存中设置好serviceId与url,定义前缀规则，然后获取到服务的具体方法

//银行的业务大厅 新增设了一个xxx功能,有必要挨个通知全国所有人吗?
//去银行大厅, 问大堂经理(网关)一下我要办xxxx业务去哪
//规则, 定义一个标识,这个标识对应着 xxx服务的xxx方法,以后我们传递这个标识到网关
//网关根据标识列表 来看看这个标识到底对应着谁


# 参数校验50
> 校验参数传入是否完全SysParamFilter

# 时间超时判断60
> 判断是否超时TimeStampFilter

# 签名校验75
> 校验签名是否正确，一般这里是进行的加密校验SignFilter
> 前端会根据method/appkey/timestamp/salt等参数计算出一个sign,并作为地址栏参数一并传给后端；
> 同样，后端也会根据method/appkey/timestamp/salt等参数计算出一个sign，然后进行比较，这就是身份校验

# 基本流程100
> 设置一个访问规则，比如 method=com.rj.test01
> 在redis缓存中，设置大key为【Apiname:com.rj.test01】为存储，里面存有两个map，一个是serviceId，就是我们的test01测试服务的名称
> 另一个是url，也就是请求地址
> 在地址栏，我们只需要按照规则传入【method=com.rj.test01&name=Jack】
> 拼接后就是【Apiname:com.rj.test01】,通过redis缓存获取到对应的serviceId和url，这里使用的是feign，回到cache方法
> 这里需要拼接，按照test01服务中的要求进行拼接，形成带有值的url
> 将serviceId和url通过【RequestContext.getCurrentContext().put】方法放入到一个map，这是会自动寻找到对应服务下的对应路径，并执行对应方法



