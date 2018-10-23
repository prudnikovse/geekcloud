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
        System.out.println("Server read...");
        try {
            if (msg == null)
                return;
            ResponseData response = new ResponseData();
            if (msg instanceof RequestData) {
                RequestData request = (RequestData) msg;
                response.setAction(request.getAction());
                switch (request.getAction()){
                    case REGISTER_USER:{
                        if(request.getData() != null && request.getData() instanceof User){
                            User user = (User)request.getData();
                            String password = user.getPassword();
                            ResultItem<User> res = registerUser(user);
                            if(res.isSuccess()){
                                authUser(user.getUserName(), password, response);
                            }else
                                response.setMessage(res.getMessage());
                        }else
                            response.setMessage("Server received wrong object in body!");
                        break;
                    }
                    case AUTH_USER:{
                        if(request.getData() != null && request.getData() instanceof String) {
                            String logopass[] = ((String)request.getData()).split("&", 2);
                            authUser(logopass[0], logopass[1], response);
                        } else
                            response.setMessage("Server received wrong object in body!");
                        break;
                    }
                    default:{
                        response.setMessage("Пользователь не прошел аутентификацию");
                    }
                }
                if(_isAuthorize){
                    ctx.pipeline().addLast(new ActionHandler(_login));
                    ctx.pipeline().remove(AuthHandler.class);
                }
            } else {
                response.setMessage("Server received wrong object!");
            }
            System.out.println("Server response...");
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

    private void authUser(String userName, String password, ResponseData response){
        _isAuthorize = UserManager.instance().validateUser(userName, password);
        _login = userName;

        response.setSuccess(_isAuthorize);
        response.setMessage(_isAuthorize ? "Вы вошли под пользователем " + userName : "Не удалось подключиться к серверу. " +
                "Проверьте имя пользователя или пароль");
    }
}
