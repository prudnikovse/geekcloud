package com.geek.cloud.client;

import com.geek.cloud.common.*;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.util.ReferenceCountUtil;

import java.net.SocketAddress;
import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;

public class ClientHandler extends ChannelDuplexHandler { // ChannelInboundHandlerAdapter {
    private ArrayBlockingQueue<ResponseData> _responses;
    private ChannelHandlerContext _ctx;

    @Override
    public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) throws Exception {
        super.connect(ctx, remoteAddress, localAddress, promise);
        _ctx = ctx;
    }

    public ClientHandler(ArrayBlockingQueue<ResponseData> responses){
        _responses = responses;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //System.out.println("Client read...");
        ResponseData response = null;
        try {
            if (msg == null)
                return;
            if (msg instanceof ResponseData) {
                response = (ResponseData) msg;

//                switch (response.getAction()){
//                    case REGISTERUSER:
//                    case AUTHUSER:{
//                        break;
//                    }
//                    case UPLOADFILE:{
//                        if(response.getData() != null && response.getData() instanceof String) {
//                            String processId = (String)response.getData();
//                            if(_currentUploads.containsKey(processId)){
//                                if(response.isSuccess()) {
//                                    _currentUploads.replace(processId, _currentUploads.get(processId).intValue() - 1);
//                                    if(_currentUploads.get(processId) == 0){
//                                        _currentUploads.remove(processId);
//                                        System.out.println("Загрузка файла завершена!");
//                                    }
//                                }
//                                else {
//                                    _currentUploads.remove(processId);
//                                    System.out.println(response.getMessage());
//                                }
//                            }
//                        }
//                        break;
//                    }
//                    default:{
//                    }
//                }
//                System.out.println(response.getMessage());
            } else {
                response = new ResponseData();
                response.setAction(Action.ERROR);
                response.setMessage("Client received wrong object!");
            }
            _responses.put(response);
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }

//    public void sendData(Object data){
//        _ctx.writeAndFlush(data);
//    }
}
