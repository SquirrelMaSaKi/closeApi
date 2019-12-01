package com.rj.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.rj.config.ElasticSearchConfig;
import com.rj.constant.MyConstant;
import com.rj.mq.SendLogMQ;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.GenericMessage;

import javax.servlet.http.HttpServletRequest;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static com.netflix.zuul.context.RequestContext.getCurrentContext;

public class PostFilter extends ZuulFilter {
    @Autowired
    private SendLogMQ sendLogMQ;
    @Autowired
    private ObjectMapper mapper;


    @Override
    public String filterType() {
        return FilterConstants.POST_TYPE;
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return getCurrentContext().sendZuulResponse();
    }

    /**
     * 记录一些信息，将用户的一些请求相关的数据传递到我们的es中，作为日志存储起来
     * 这些信息都是EalsticSearchConfig中的配置信息
     */
    @Override
    public Object run() throws ZuulException {
        //创建EalsticSearchConfig
        ElasticSearchConfig elasticSearchConfig = new ElasticSearchConfig();

        //创建request
        RequestContext context = RequestContext.getCurrentContext();
        HttpServletRequest request = context.getRequest();

        //获取request中与EalsticSearchConfig配置相关的信息，并set
        //获取当前时间，用于计算平台响应时间，因为是PostFilter，已经是最后的处理了
        long currentTimeMillis = System.currentTimeMillis();

        //获取接收请求的时间，这个时间在请求正文context中
        long receiveTime = (long) context.get(MyConstant.RECEIVE_TIME);

        //存入接收请求的时间
        elasticSearchConfig.setReceiveTime(receiveTime);

        //存入平台响应时间
        elasticSearchConfig.setPlatformTotalTime(currentTimeMillis - receiveTime);

        //存入appkey
        String appkey = request.getParameter(MyConstant.APPKEYPARAMETERNAME);
        elasticSearchConfig.setAppKey(appkey);

        //存入method
        String method = request.getParameter(MyConstant.METHODPARAMETERNAME);
        elasticSearchConfig.setMethodName(method);

        //存入错误码
        elasticSearchConfig.setErrorCode("000000");

        //存入客户端地址
        elasticSearchConfig.setClientIp(request.getRemoteAddr());

        //存入服务器地址
        elasticSearchConfig.setServerIp(localIp());

        //存入请求内容正文
        elasticSearchConfig.setRequestContent(request.getParameter(MyConstant.CONTENTPARAMETERNAME));

        //将我们的日志信息转为json字符串，由mq发送出去
        try {
            String logJson = mapper.writeValueAsString(elasticSearchConfig);
            MessageChannel channel = sendLogMQ.getChannel();
            channel.send(new GenericMessage<>(logJson));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String localIp() {
        InetAddress inetAddress = null;

        try {
            inetAddress = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        return inetAddress.getHostAddress();
    }
}
