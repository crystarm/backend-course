package com.karandashov;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.karandashov.controller.NewsEventController;
import com.karandashov.repository.NewsEventRepository;
import com.karandashov.service.NewsEventService;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import tools.jackson.databind.json.JsonMapper;

public class Main {

    private static final int PORT = 8080;

    public static void main(String[] args) throws Exception {
        // Создаем JsonMapper для работы с JSON
        JsonMapper jsonMapper = JsonMapper.builder()
                .changeDefaultVisibility(v -> v.withFieldVisibility(JsonAutoDetect.Visibility.ANY))
                .build();

        // Создаем репозиторий и сервис
        NewsEventRepository repository = new NewsEventRepository();
        NewsEventService service = new NewsEventService(repository);
        NewsEventController controller = new NewsEventController(service, jsonMapper);

        // Настраиваем и запускаем Netty сервер
        EventLoopGroup bossGroup = new NioEventLoopGroup(4); // 4 потока
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ch.pipeline().addLast(
                                    new HttpServerCodec(),
                                    new HttpObjectAggregator(65536),
                                    new HttpRequestHandler(controller)
                            );
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            // Запускаем сервер
            ChannelFuture f = b.bind(PORT).sync();
            System.out.println("Netty server started on port " + PORT);

            // Ждем закрытия канала сервера
            f.channel().closeFuture().sync();
        } finally {
            // Освобождаем ресурсы
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}
