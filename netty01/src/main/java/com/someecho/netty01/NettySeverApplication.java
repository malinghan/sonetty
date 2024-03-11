package com.someecho.netty01;

/**
 * @author someecho <linghan.ma@gmail.com>
 * Created on 2024-03-11
 */
public class NettySeverApplication {

    public static void main(String[] args) {
        HttpServer server = new HttpServer(false, 8808);
        try {
            server.run();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
