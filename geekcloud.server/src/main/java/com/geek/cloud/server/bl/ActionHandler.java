package com.geek.cloud.server.bl;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import com.geek.cloud.common.*;

public class ActionHandler extends ChannelInboundHandlerAdapter {
    private String _login;

    public ActionHandler(String login){
        _login = login;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("Client read...");
        try {
            if (msg == null)
                return;
            ResponseData response = null;
            if (msg instanceof RequestData) {
                RequestData request = (RequestData) msg;
                switch (request.getAction()){
                    case UPLOADFILE:
                    case DOWNLOADFILE:{
                        //TODO Передаем в FileServerHandler
                        break;
                    }
                    default:{
                        return;
                    }
                }
            } else {
                System.out.printf("Server received wrong object!");
                response = new ResponseData();
                response.setAction(Action.ERROR);
                response.setMessage("Server received wrong object!");
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }
}
