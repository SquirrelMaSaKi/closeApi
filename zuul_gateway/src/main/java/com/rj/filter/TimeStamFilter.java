package com.rj.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 用于过期时间的判断，比如超过一定时间，显示超时，需要重新访问
 */
@Component
public class TimeStamFilter extends ZuulFilter {
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return 60;
    }

    @Override
    public boolean shouldFilter() {
        return RequestContext.getCurrentContext().sendZuulResponse();
    }

    @Override
    public Object run() throws ZuulException {
        //获取请求的时间戳，所以这里需要有参数timestamp
        RequestContext context = RequestContext.getCurrentContext();
        HttpServletRequest request = context.getRequest();
        String timestamp = request.getParameter("timestamp");
        try {
            Date date = simpleDateFormat.parse(timestamp);
            //获取当前时间
            long currentTime = System.currentTimeMillis();
            //传入的时间戳
            long dateTime = date.getTime();
            if (currentTime-dateTime > 3600000 || currentTime-dateTime < 0) {
                context.getResponse().setContentType("text/html;charset=utf-8");
                context.setSendZuulResponse(false);
                context.setResponseBody("超出时间范围");
            }
        } catch (ParseException e) {
            e.printStackTrace();
            //代表不是合法时间戳类型
            context.getResponse().setContentType("text/html;charset=utf-8");
            context.setSendZuulResponse(false);
            context.setResponseBody("非法时间戳格式");
        }
        return null;
    }
}
