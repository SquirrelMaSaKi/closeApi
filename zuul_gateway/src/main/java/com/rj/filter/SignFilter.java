package com.rj.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.rj.constant.MyConstant;
import com.rj.exception.ExceptionDict;
import com.rj.feign.MyFeign;
import com.rj.util.Md5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Component
public class SignFilter extends ZuulFilter {
    private boolean isEnable = true;

    @Autowired
    private MyFeign myFeign;

    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return 75;
    }

    @Override
    public boolean shouldFilter() {
        //不是说一定要用zuul里面的sendresponse来设置,这里就是要一个boolean值,你能通过任何方式做操作
        RequestContext context = RequestContext.getCurrentContext();
        //可以进行判断，比如当context.sendZuulResponse()&&isEnable 两个条件都满足的时候才能为true
        //我们的这个条件可能是在多个地方都可以进行设置的,所以应该是任何一地方是false了就都不可以用
        return context.sendZuulResponse();
        //return false;
    }

    @Override
    public Object run() throws ZuulException {
        //1、获取请求参数
        RequestContext context = RequestContext.getCurrentContext();
        HttpServletRequest request = context.getRequest();

        //2、将请求参数中签名数据拿出来sign，避免重复提交判断，这里设置常量标记
        String sign = request.getParameter(MyConstant.SIGNPARAMETERNAME);
        boolean zmember = myFeign.isZmember(MyConstant.REPETITIONCODE, sign);
        if(zmember) {
            context.setSendZuulResponse(false);
            context.getResponse().setContentType("text/html;charset=utf-8");
            context.setResponseBody("{\"code\":"+ ExceptionDict.CFTJCS+",\"msg\":"+"请不要重复提交"+"}");
            return null;
        }

        //3、生成新的map，里面包含的是name=zhangsan / age=20之类的数据，但是不能有sign
        Map<String, String> map = convertMap(request);

        //4、找到这个人对应的盐
        //这里注意，盐不能和帐号有关，一个账号下面可以有多个应用,如果和账号相关,修改完成后,每个应用都需要修改代码
        //我们的设计原则是：修改影响尽量少的内容
        //既然每个应用都有独立的盐，那么我们可以以appkey作为key，在redis中存放对应盐值
        String appkey = map.get(MyConstant.APPKEYPARAMETERNAME); //获取用户传过来的appkey
        //根据用户传来的appkey，拼接字符串，从redis缓存中获取对应数据
        Map<Object, Object> appinfo = myFeign.hget(MyConstant.APPKEYPRIFIX + appkey);
        //获取盐值
        String salt = appinfo.get("salt").toString();
        //利用新的map和盐算出一个sign
        String signature = Md5Util.md5Signature(map, salt);
        //打印，这里我们不演示前端计算sign的方法，我们只需要赋值粘贴即可
        System.out.println("生成的签名是："+signature+",传递的签名是："+sign);
        //比较传递过来的 sign和刚刚生成的sign
        if (!sign.equalsIgnoreCase(signature)) {
            context.setSendZuulResponse(false);
            context.getResponse().setContentType("text/html;charset=utf-8");
            context.setResponseBody("{\"code\":"+ ExceptionDict.SIGNERROR+",\"msg\":"+"签名校验失败"+"}");
            return null;
        }
        //保存到zset中，为了避免重复提交
        myFeign.zSave(MyConstant.REPETITIONCODE, sign, System.currentTimeMillis());
        return null;
    }

    Map<String, String> convertMap(HttpServletRequest request) {
        Map<String,String> map = new HashMap<>();
        Enumeration<String> names = request.getParameterNames();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            //这里要排除掉sign
            if (MyConstant.SIGNPARAMETERNAME.equalsIgnoreCase(name)) {
                continue;
            } else {
                map.put(name, request.getParameter(name));
            }
        }
        return map;
    }
}
