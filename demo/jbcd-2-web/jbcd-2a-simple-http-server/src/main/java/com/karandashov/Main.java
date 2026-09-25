package com.karandashov;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;

public class Main {

    private static final int PORT = 8080;

    public static void main(String[] args) throws IOException {
        // HttpServer входит в JRE: для запуска HTTP-сервера не нужна внешняя web-библиотека
        HttpServer server = new SimpleHttpServerApp().createServer(PORT);
        server.start();

        System.out.println("Simple HTTP server started on port " + PORT);
    }
}
