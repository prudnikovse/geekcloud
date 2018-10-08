package com.geek.cloud.client;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.bootstrap.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.channel.*;

import java.net.InetSocketAddress;

public class Network {
    private static Object _InstanceLock = new Object();
    private static volatile Network _Instance;

    private Channel _channel;

    private static final int PORT = 8189;
    private static final int MAX_OBJ_SIZE = 1024 * 1024 * 100; // 10 mb

    private Network(){
    }

    public static Network instance(){
        if(_Instance == null){
            synchronized (_InstanceLock){
                if(_Instance == null){
                    _Instance = new Network();
                }
            }
        }
        return _Instance;
    }

    public void start(){
        EventLoopGroup workerGroup = new NioEventLoopGroup();  //Создается тред-пулл,
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(workerGroup);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.remoteAddress(new InetSocketAddress("localhost", PORT));
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    socketChannel.pipeline().addLast(
                            new ObjectDecoder(MAX_OBJ_SIZE, ClassResolvers.cacheDisabled(null)),
                            new ObjectEncoder(),
                            new ClientHandler()
                    );
                    _channel = socketChannel;
                }
            });

            ChannelFuture channelFuture = bootstrap.connect().sync();
            channelFuture.channel().closeFuture().sync();
        }catch (Exception ex){
            ex.printStackTrace();
        }finally {
            workerGroup.shutdownGracefully();
        }
    }

    public Channel getCurrentChannel() {
        return _channel;
    }

    public void sendData(Object data){
        _channel.writeAndFlush(data);
    }

    public boolean isConnectionOpen(){
        return _channel != null && _channel.isActive();
    }

    public void closeConnection(){
        _channel.close();
    }

    public boolean isLogin(){
        if(isConnectionOpen()){
            ClientHandler clientHandler = _channel.pipeline().get(ClientHandler.class);
            if (clientHandler != null)
                return clientHandler.isLogin();
        }
        return false;
    }
}
