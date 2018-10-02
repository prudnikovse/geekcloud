package com.geek.cloud.server.bl;

import com.geek.cloud.common.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

public class AuthHandler extends ChannelInboundHandlerAdapter {
    private boolean _isAuthorize;
    private String _login = null;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Client connected...");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            if (msg == null)
                return;
            ResponseData response = null;
            if (msg instanceof RequestData) {
                RequestData request = (RequestData) msg;
                switch (request.getAction()){
                    case REGISTERUSER:{
                        if(request.getData() != null && request.getData() instanceof User){
                            ResultItem<User> regRes = registerUser((User)request.getData());
                            if(regRes.isSuccess()){
                                User user = regRes.getData();
                                response = authUser(user.getUserName(), user.getPassword());
                            }else{
                                response = new ResponseData();
                                response.setMessage(regRes.getMessage());
                            }
                        }
                        break;
                    }
                    case AUTHUSER:{
                        if(request.getData() != null && request.getData() instanceof String) {
                            String logopass[] = ((String)request.getData()).split("&", 2);
                            response = authUser(logopass[0], logopass[1]);
                        }
                        break;
                    }
                    default:{
                        response = new ResponseData();
                        response.setMessage("Пользователь не прошел аутентификацию");
                    }
                }
                response.setAction(request.getAction());
                if(_isAuthorize){
                    ctx.pipeline().addLast(new ActionHandler(_login));
                    ctx.pipeline().remove(AuthHandler.class);
                }
            } else {
                System.out.printf("Server received wrong object!");
                response = new ResponseData();
                response.setMessage("Server received wrong object!");
            }
            ctx.writeAndFlush(response);
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }

    private ResultItem<User> registerUser(User user){
        ResultItem<User> res = UserManager.instance().createUser(user);

        return res;
    }

    private ResponseData authUser(String userName, String password){
        _isAuthorize = UserManager.instance().validateUser(userName, password);
        _login = userName;

        ResponseData response = new ResponseData();
        response.setSuccess(_isAuthorize);
        return response;
    }
}
