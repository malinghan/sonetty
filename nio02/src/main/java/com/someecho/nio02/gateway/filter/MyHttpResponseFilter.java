package com.someecho.nio02.gateway.filter;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpUtil;

import static io.netty.handler.codec.http.HttpResponseStatus.NO_CONTENT;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

/**
 * @author : linghan.ma
 * @Package io.github.kimmking.gateway.filter
 * @Description:
 * @date Date : 2020年11月03日 2:24 AM
 **/
public class MyHttpResponseFilter implements HttpResponseFilter {

    @Override
    public void filter(FullHttpRequest fullRequest, ChannelHandlerContext ctx, HttpResponse endpointResponse) throws Exception{
        handleResponse(fullRequest,ctx,endpointResponse);
    }

    public void handleResponse(final FullHttpRequest fullRequest, final ChannelHandlerContext ctx, final HttpResponse endpointResponse) throws Exception {
        FullHttpResponse response = null;
        try {
            byte[] body = EntityUtils.toByteArray(endpointResponse.getEntity());
            //对response进行过滤、改造
            String bodyString = new String(body)+"abcdefg";
            System.out.println(bodyString);
            response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(bodyString.getBytes()));
            response.headers().set("Content-Type", "application/json");
            response.headers().setInt("Content-Length",15);
//            response.headers().setInt("Content-Length", Integer.parseInt(endpointResponse.getFirstHeader("Content-Length").getValue()));
            //对response进行处理
            //endpointResponse copy到FullHttpResponse,并打印
//            for (Header e : endpointResponse.getAllHeaders()) {
//                response.headers().set(e.getName(),e.getValue());
//                System.out.println(e.getName() + " => " + e.getValue());
//            }

        } catch (Exception e) {
            e.printStackTrace();
            response = new DefaultFullHttpResponse(HTTP_1_1, NO_CONTENT);
            exceptionCaught(ctx, e);
        } finally {
            if (fullRequest != null) {
                if (!HttpUtil.isKeepAlive(fullRequest)) {
                    ctx.write(response).addListener(ChannelFutureListener.CLOSE);
                } else {
                    //response.headers().set(CONNECTION, KEEP_ALIVE);
                    ctx.write(response);
                }
            }
            ctx.flush();
            //ctx.close();
        }

    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
