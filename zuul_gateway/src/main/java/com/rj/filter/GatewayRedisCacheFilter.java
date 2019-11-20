package com.rj.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.rj.constant.MyConstant;
import com.rj.exception.ExceptionDict;
import com.rj.feign.MyFeign;
import org.apache.http.HttpStatus;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.Map;

/**
 * 我们在配置中设置了所有服务不会被网关直接发现，而是进入到redisCache服务中
 * 我们需要进行过滤拦截
 */
@Component
public class GatewayRedisCacheFilter extends ZuulFilter {
    @Resource
    private MyFeign myFeign;

    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return 100;
    }

    @Override
    public boolean shouldFilter() {
        return RequestContext.getCurrentContext().sendZuulResponse();
    }

    @Override
    public Object run() throws ZuulException {
        /**
         * 我们定义了一套规则：
         * 用户传递指定的method参数过来，然后我们预先规定了method值以及服务和地址的关系
         * 根据用户传过来的值来判定我们到底使用哪个服务的哪个方法
         * 在内部进行跳转
         */
        RequestContext context = RequestContext.getCurrentContext();
        HttpServletRequest request = context.getRequest();
        String method = request.getParameter("method");

        Map<Object, Object> map = myFeign.hget(MyConstant.APIPRIFIX + method);

        //如果传递的method是错误的,没有找到对应的服务,应该要拦截请求,我们在缓存中会存储两个东西，一个是serviceId，一个是url
        //所以必须至少是两个结果
        if (map == null || map.size() < 2) {
            context.setSendZuulResponse(false);
            context.getResponse().setContentType("text/html;charset=utf-8");
            context.setResponseStatusCode(HttpStatus.SC_OK);//设置状态码，200
            //注意，在filter和拦截器中，ControllerAdvice全局异常解析器就失效了。。。。,我们需要手动抛给页面
            String code = ExceptionDict.ROUTE_ERROR;
            String msg = "找不到路由";
            context.setResponseBody("{\"code\":" + code + ",\"msg\":" + msg + "}");
            return null; //这里只能为空，不能抛出
        } else {
            Object serviceId = map.get("serviceId");
            Object url = map.get("url");

            //打印服务id和url
            System.out.println(serviceId + "  ------url===>" + url); //zuulTest01  ------url===>/test/{name}
            //设置要跳转的服务的名字
            context.put(FilterConstants.SERVICE_ID_KEY, serviceId);
            //我们需要用真正的参数替换掉url中的{name}的值
            //要想替换必须有规则,有规律,没有规律的话就要自定义规律,注意编程世界没有任何已存在的规律,都是我们自己定义的
            //比如/test/test/{name}/{age}/{address} 我们要求你传递的参数也是name age address，形式是：?name=zhangsan&age=20
            Enumeration<String> parameterNames = request.getParameterNames();   //获取所有参数的名字
            while (parameterNames.hasMoreElements()) {
                String pName = parameterNames.nextElement(); //获取每个参数名，比如name
                //这里注意，method=xxx这个用来从缓存中获取缓存服务与方法的所以必须排除
                if (pName.equalsIgnoreCase("method")) {
                    continue;
                }
                String name = request.getParameter(pName); //请求参数的值 比如zhangsan
                pName = "{"+pName+"}"; //这样就替换成了{zhangsan}
                //这里注意使用replace而不是replaceAll，区别在于后者是正则匹配的会出错
                url = url.toString().replace(pName, name); //这样参数就变成了/zhangsan/20
            }
            context.put(FilterConstants.REQUEST_URI_KEY, url);
        }
        return null;
    }
}
