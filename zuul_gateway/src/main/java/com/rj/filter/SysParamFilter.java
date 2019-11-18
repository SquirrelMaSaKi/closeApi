package com.rj.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.rj.constant.MyConstant;
import com.rj.exception.ExceptionDict;
import com.rj.exception.GateWayException;
import com.rj.feign.MyFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * 用于校验某些参数是否传递的过滤器
 */
@Component
public class SysParamFilter extends ZuulFilter {
    @Autowired
    private MyFeign myFeign;

    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return 50;
    }

    @Override
    public boolean shouldFilter() {
        return RequestContext.getCurrentContext().sendZuulResponse();
    }

    /**
     * 这里我们定义规则
     * 必须有appkey进行校验
     */
    @Override
    public Object run() throws ZuulException {
        RequestContext context = RequestContext.getCurrentContext();
        //虽然members是由我们开发人员或者是运维人员添加到redis中,并且不依赖于用户传递的参数,所以理论上这个集合一定有值,但是为了安全性,还是判断一下
        Set<String> members = myFeign.sMember(MyConstant.PARAMSPRIFIX + "publicparam");
        //我们在redis中放入了method和appkey，所以在地址栏中这两个参数必须都有，否则校验不通过
        if(members!=null&&members.size()>0) {
            for (String member : members) {
                //需要判断必须的参数有没有填写
                //member是redis缓存中自带的key值
                //这个value是根据我们redis缓存中的key值去获取用户传过来的value，比如&appkey=123
                String value = context.getRequest().getParameter(member);
                if(value==null || value.trim().length()==0) {
                    //没有传递参数，则校验不通过
                    context.setSendZuulResponse(false);
                    //注意，在filter和拦截器中，ControllerAdvice全局异常解析器就失效了。。。。,我们需要手动抛给页面
                    context.getResponse().setContentType("text/html;charset=utf-8");
                    String res = "{\"code\":" + ExceptionDict.PARAMSERROR + ",\"msg\":参数名" + member + "为空" + "}";
                    context.setResponseBody(res);
                    return null;
                }
            }
        }
        return null;
    }
}
