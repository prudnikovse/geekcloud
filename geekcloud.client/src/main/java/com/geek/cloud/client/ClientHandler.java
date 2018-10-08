package com.geek.cloud.client;

import com.geek.cloud.common.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

public class ClientHandler extends ChannelInboundHandlerAdapter {
    private boolean _isLogin;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("Client read...");
        try {
            if (msg == null)
                return;
            if (msg instanceof ResponseData) {
                ResponseData response = (ResponseData) msg;
                switch (response.getAction()){
                    case REGISTERUSER:
                    case AUTHUSER:{
                        _isLogin = response.isSuccess();
                        break;
                    }
                    case UPLOADFILE:{
                        break;
                    }
                    default:{
                    }
                }
                System.out.println(response.getMessage());
            } else {
                System.out.println("Client received wrong object!");
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }

    public boolean isLogin(){
        return _isLogin;
    }
}
