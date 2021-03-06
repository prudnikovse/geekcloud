package com.geek.cloud.server.bl;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import com.geek.cloud.common.*;

import java.io.IOException;

public class ActionHandler extends ChannelInboundHandlerAdapter {
    private String _login;

    private FileManager _fileManager;

    public ActionHandler(String login) throws IOException {
        _login = login;
        _fileManager = new FileManager(_login);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            if (msg == null)
                return;
            ResponseData response = new ResponseData();;
            if (msg instanceof RequestData) {
                RequestData request = (RequestData) msg;
                response.setAction(request.getAction());
                switch (request.getAction()){
                    case UPLOAD_FILE:{
                        if(request.getData() != null && request.getData() instanceof FileData) {
                            FileData fileData = (FileData)request.getData();
                            try {
                                _fileManager.downloadFile(fileData);
                                response.setSuccess(true);
                                response.setData(fileData.getProcessId());
                            }catch (Exception ex){
                                response.setMessage("Ошибка загрузки файла " + fileData.getFileName() + " !");
                            }
                        }
                        break;
                    }
                    case DOWNLOAD_FILE_START:
                    case DOWNLOAD_FILE:{
                        if(request.getData() != null && request.getData() instanceof String[]) {
                            FileData fileData;
                            try {
                                String[] params = (String[])request.getData();
                                if(request.getAction().equals(Action.DOWNLOAD_FILE_START) && params.length > 1){
                                    fileData = _fileManager.uploadFile(params[0], params[1]);
                                }else {
                                    fileData = _fileManager.uploadFile(params[0]);
                                }
                                response.setData(fileData);
                                response.setSuccess(true);
                            }catch (Exception ex){
                                response.setMessage("Ошибка загрузки файла!");
                            }
                        }
                        break;
                    }
                    case RENAME_FILE:{
                        if(request.getData() != null && request.getData() instanceof String[]) {
                            String[] params = (String[])request.getData();
                            response.setSuccess(_fileManager.renameFile(params[0], params[1]));
                        }
                        break;
                    }
                    case DELETE_FILE:{
                        if(request.getData() != null && request.getData() instanceof String) {
                            response.setSuccess(_fileManager.deleteFile((String)request.getData()));
                        }
                        break;
                    }
                    case CREATE_DIRECTORY:{
                        if(request.getData() != null && request.getData() instanceof String) {
                            _fileManager.createDirectory((String)request.getData());
                            response.setSuccess(true);
                        }
                        break;
                    }
                    case GET_FILE_LIST:{
                        if(request.getData() != null && request.getData() instanceof String) {
                            response.setData(_fileManager.getFileList((String)request.getData()));
                            response.setSuccess(true);
                        }
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

            ctx.writeAndFlush(response);
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        _fileManager.close();
    }
}
