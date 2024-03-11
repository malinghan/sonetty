package com.someecho.nio02.gateway.router;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author : linghan.ma
 * @Package io.github.kimmking.gateway.router
 * @Description:
 * @date Date : 2020年11月03日 2:32 AM
 **/
public class MyHttpEndpointRouter implements HttpEndpointRouter {

    private static AtomicInteger counter = new AtomicInteger(1);

    @Override
    public String route(List<String> endpoints) {
        counter.addAndGet(1);
        System.out.println("counter:"+counter.get());
        return (counter.get() % 3 == 0) ? endpoints.get(0):endpoints.get(1);
    }
}
