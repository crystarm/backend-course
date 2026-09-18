package com.karandashov;

import com.sun.net.httpserver.HttpServer;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.net.InetSocketAddress;

public class SimpleHttpServerApp {

    public HttpServer createServer(int port) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", new NewsEventHttpHandler(JsonMapper.builder().build()));
        server.setExecutor(null); // встроенный executor с одним потоком на запрос
        return server;
    }
}
