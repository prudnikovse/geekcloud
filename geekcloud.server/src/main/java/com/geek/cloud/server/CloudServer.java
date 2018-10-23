package com.geek.cloud.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import com.geek.cloud.server.bl.*;

public class CloudServer {
    private static final int PORT = 8189;
    private static final int MAX_OBJ_SIZE = 1024 * 1024 * 10; // 10 mb

    public void run() throws Exception {
        EventLoopGroup mainGroup = new NioEventLoopGroup(); //Создаются тред-пуллы (отвечает за подключающихся клиентов, ожидает новых клиентов)
        EventLoopGroup workerGroup = new NioEventLoopGroup(); //группа потоков для обработки сетевых подключений
        try {
            ServerBootstrap b = new ServerBootstrap(); // Отвечает за настройку сервера
            b.group(mainGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    // .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(
                                    new ObjectDecoder(MAX_OBJ_SIZE, ClassResolvers.cacheDisabled(null)),
                                    new ObjectEncoder(),
                                    new AuthHandler()
                            );
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    //.option(ChannelOption.TCP_NODELAY, true)
                    .childOption(ChannelOption.SO_KEEPALIVE, true); //Держим соединение если оно не активно
            ChannelFuture future = b.bind(PORT).sync();
            future.channel().closeFuture().sync();
        } finally {
            mainGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        new CloudServer().run();
    }
}
