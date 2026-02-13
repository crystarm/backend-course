package com.karandashov;

import com.karandashov.controller.NewsEventController;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

/**
 * Обработчик HTTP запросов, который делегирует обработку контроллеру
 */
public class HttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private final NewsEventController controller;

    public HttpRequestHandler(NewsEventController controller) {
        this.controller = controller;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) {
        // Обрабатываем запрос с помощью контроллера
        FullHttpResponse response = controller.handleRequest(request);
        
        // Отправляем ответ клиенту
        ctx.writeAndFlush(response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}