package com.rj.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.rj.constant.MyConstant;
import com.rj.exception.ExceptionDict;
import com.rj.exception.GateWayException;
import com.rj.feign.MyFeign;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 我们在配置中设置了所有服务不会被网关直接发现，而是进入到redisCache服务中
 * 我们需要进行过滤拦截
 */
public class GatewayRedisCacheFilter extends ZuulFilter {
    @Autowired
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
        if (map==null || map.size()<2) {
            context.setSendZuulResponse(false);
            context.getResponse().setContentType("text/html;charset=utf-8");
            context.setResponseStatusCode(HttpStatus.SC_OK);//设置状态码，200
            throw new GateWayException(ExceptionDict.ROUTE_ERROR,"找不到路由");
        } else {
            Object serviceId = map.get("serviceId");
            Object url = map.get("url");
            System.out.println(serviceId + "  ------url===>" + url);
        }

        return null;
    }
}
