package com.someecho.netty01;

import static io.netty.handler.codec.http.HttpHeaderNames.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaderValues.KEEP_ALIVE;

import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.ReferenceCountUtil;

/**
 * @author someecho <linghan.ma@gmail.com>
 * Created on 2024-03-11
 */
public class HttpHandler extends ChannelInboundHandlerAdapter {
    public static Logger logger = LoggerFactory.getLogger(HttpHandler.class);
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            long startTime = System.currentTimeMillis();
            logger.info("channelRead流量接口请求开始，时间为:{}", startTime);
            FullHttpRequest fullHttpRequest = (FullHttpRequest)  msg;
            String uri = fullHttpRequest.uri();
            logger.info("接收到用户请求url为{}", uri);
            if (uri.contains("/test")) {
                handlerTest(fullHttpRequest, ctx);
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
     }
    private void handlerTest(FullHttpRequest fullHttpRequest, ChannelHandlerContext ctx) {
        FullHttpResponse response = null;
       try {
           logger.info("开始处理用户请求");
           String value = "someecho";
           response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK,
                   Unpooled.wrappedBuffer(value.getBytes(StandardCharsets.UTF_8)));
           response.headers().set("content-Tyoe", "application/json");
           response.headers().setInt("Content-Length", response.content().readableBytes());
       } catch (Exception e) {
           logger.error("处理测试接口出错", e);
           response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NO_CONTENT);
       } finally {
           if (fullHttpRequest != null) {
               if(!HttpUtil.isKeepAlive(fullHttpRequest)) {
                   ctx.write(response).addListener(ChannelFutureListener.CLOSE);
               } else {
                   response.headers().set(CONNECTION, KEEP_ALIVE);
                   ctx.write(response);
               }
           }
       }
    }
}
