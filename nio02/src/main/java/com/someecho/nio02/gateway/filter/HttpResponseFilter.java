package com.someecho.nio02.gateway.filter;


import org.apache.http.HttpResponse;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;

/**
 * @author : linghan.ma
 * @Package io.github.kimmking.gateway.filter
 * @Description:
 * @date Date : 2020年11月03日 2:23 AM
 **/
public interface HttpResponseFilter {

    void filter(final FullHttpRequest fullRequest, final ChannelHandlerContext ctx, final HttpResponse endpointResponse) throws Exception;
}
