package com.geek.cloud.client;

import com.geek.cloud.common.RequestData;
import com.geek.cloud.common.ResponseData;
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
import java.util.concurrent.ArrayBlockingQueue;

public class Network {
    private static Object _InstanceLock = new Object();
    private static volatile Network _Instance;

    private static final int PORT = 8189;
    private static final int MAX_OBJ_SIZE = 1024 * 1024 * 1000; // 1000 mb
    private static final int QUEUE_SIZE = 10000;

    private final ArrayBlockingQueue<ResponseData> _responses = new ArrayBlockingQueue<>(QUEUE_SIZE);

    private Channel _channel;
    private ClientHandler _clientHandler;

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
            _clientHandler = new ClientHandler(_responses);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    socketChannel.pipeline().addLast(
                            new ObjectDecoder(MAX_OBJ_SIZE, ClassResolvers.cacheDisabled(null)),
                            new ObjectEncoder(),
                            _clientHandler
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

    public void sendData(RequestData request){
        _channel.writeAndFlush(request);
    }

    public ResponseData getResponse() throws InterruptedException{
        return _responses.take();
    }

    public boolean isConnectionOpen(){
        return _channel != null && _channel.isActive();
    }

    public void closeConnection(){
        _channel.close();
    }
}
