package com.someecho.nio02.gateway.outbound.httpclient4;


import io.netty.channel.*;
import io.netty.handler.codec.http.FullHttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.protocol.HTTP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

import com.someecho.nio02.gateway.filter.HttpResponseFilter;
import com.someecho.nio02.gateway.filter.MyHttpResponseFilter;


public class HttpOutboundHandler extends ChannelOutboundHandlerAdapter {

    private static  Logger logger = LoggerFactory.getLogger(HttpOutboundHandler.class);
    
    private CloseableHttpAsyncClient httpclient;
    private ExecutorService proxyService;
    private String backendUrl;
    private HttpResponseFilter responseFilter;

    public HttpOutboundHandler(String backendUrl){
        this.backendUrl = backendUrl.endsWith("/")?backendUrl.substring(0,backendUrl.length()-1):backendUrl;
        this.responseFilter = new MyHttpResponseFilter();
        int cores = Runtime.getRuntime().availableProcessors() * 2;
        long keepAliveTime = 1000;
        int queueSize = 2048;
        RejectedExecutionHandler handler = new ThreadPoolExecutor.CallerRunsPolicy();//.DiscardPolicy();
        //用于请求真实后端服务的线程池
        proxyService = new ThreadPoolExecutor(cores, cores,
                keepAliveTime, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(queueSize),
                new NamedThreadFactory("proxyService"), handler);
        //nio reactor模型的httpclient的配置参数
        IOReactorConfig ioConfig = IOReactorConfig.custom()
                .setConnectTimeout(1000)
                .setSoTimeout(1000)
                .setIoThreadCount(cores)
                .setRcvBufSize(32 * 1024)
                .build();
        
        httpclient = HttpAsyncClients.custom().setMaxConnTotal(40)
                .setMaxConnPerRoute(8)
                .setDefaultIOReactorConfig(ioConfig)
                .setKeepAliveStrategy((response,context) -> 6000)
                .build();
        httpclient.start();
    }
    
    public void handle(final FullHttpRequest fullRequest, final ChannelHandlerContext ctx) {
        final String url = this.backendUrl + fullRequest.uri();
        System.out.println("real url:"+url);
        proxyService.submit(()->fetchGet(fullRequest, ctx, url));
    }
    
    private void fetchGet(final FullHttpRequest inbound, final ChannelHandlerContext ctx, final String url) {
        final HttpGet httpGet = new HttpGet(url);
//        httpGet.setHeader(HTTP.CONN_DIRECTIVE, HTTP.CONN_CLOSE);
        httpGet.setHeader(HTTP.CONN_DIRECTIVE, HTTP.CONN_KEEP_ALIVE);
        httpclient.execute(httpGet, new FutureCallback<HttpResponse>() {
            @Override
            public void completed(final HttpResponse endpointResponse) {
                try {
                    responseFilter.filter(inbound,ctx,endpointResponse);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    
                }
            }
            
            @Override
            public void failed(final Exception ex) {
                httpGet.abort();
                ex.printStackTrace();
            }
            
            @Override
            public void cancelled() {
                httpGet.abort();
            }
        });
    }
    
}
