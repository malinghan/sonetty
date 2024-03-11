package com.someecho.nio02.gateway.filter;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author : linghan.ma
 * @Package io.github.kimmking.gateway.filter
 * @Description:
 * @date Date : 2020年11月03日 2:12 AM
 **/
public class MyHttpRequestFilter implements HttpRequestFilter {
    private static Logger logger = LoggerFactory.getLogger(MyHttpRequestFilter.class);

    @Override
    public void filter(FullHttpRequest fullRequest, ChannelHandlerContext ctx) {
            handleRequest(fullRequest,ctx);
    }

        private void handleRequest(FullHttpRequest fullRequest, ChannelHandlerContext ctx) {
            fullRequest.headers().set("nio","malinghan");
            fullRequest.headers().set("Authorization","niomalinghan");
            //endpointResponse copy到FullHttpResponse,并打印
//            for (Map.Entry<String,String> e : fullRequest.headers()) {
//                httpRequest.headers().set(e.getKey(),e.getValue());
//                System.out.println(e.getKey() + " => " + e.getValue());
//            }
    }
}
