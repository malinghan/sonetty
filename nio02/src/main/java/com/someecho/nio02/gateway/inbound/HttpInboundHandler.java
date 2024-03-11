package com.someecho.nio02.gateway.inbound;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import com.someecho.nio02.gateway.filter.HttpRequestFilter;
import com.someecho.nio02.gateway.filter.MyHttpRequestFilter;
import com.someecho.nio02.gateway.outbound.httpclient4.HttpOutboundHandler;
import com.someecho.nio02.gateway.router.MyHttpEndpointRouter;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.util.ReferenceCountUtil;


public class HttpInboundHandler extends ChannelInboundHandlerAdapter {

    private static Logger logger = LoggerFactory.getLogger(HttpInboundHandler.class);

    private HttpOutboundHandler handler;

    private HttpRequestFilter requestFilter;

    public HttpInboundHandler(List<String> endpoints) {
        handler = new HttpOutboundHandler(new MyHttpEndpointRouter().route(endpoints));
        requestFilter = new MyHttpRequestFilter();
    }
    
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        long startTime = System.currentTimeMillis();
        try {
            logger.info("channelRead流量接口请求开始，时间为{}", startTime);
            FullHttpRequest fullRequest = (FullHttpRequest) msg;
            requestFilter.filter(fullRequest,ctx);
            handler.handle(fullRequest, ctx);
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            //https://segmentfault.com/q/1010000005026161/a-1020000005035723
            //从InBound里面读取的ByteBuf要手动释放,还有自己创建的ByteBuf要自己负责释放
            ReferenceCountUtil.release(msg);
        }
    }



    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

}
