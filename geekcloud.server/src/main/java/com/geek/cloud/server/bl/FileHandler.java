package com.geek.cloud.server.bl;

import com.geek.cloud.common.Action;
import com.geek.cloud.common.FileData;
import com.geek.cloud.common.RequestData;
import com.geek.cloud.common.bl.*;
import com.geek.cloud.common.ResponseData;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import java.util.HashMap;

public class FileHandler extends ChannelInboundHandlerAdapter {
    private static final int BUF_SIZE = 1024 * 10; // 10 mb
    private String _uploadPath = "./geekcloud.server/files/";
    private String _login = null;
    private HashMap<String, FileDownloadManager>  _downloadQueue;

    public FileHandler(String login){
        _login = login;
        _uploadPath += login + "/";
        _downloadQueue = new HashMap<>();
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
                    case UPLOADFILE: {
                        if(request.getData() != null && request.getData() instanceof FileData){
                            FileData fileData = (FileData)request.getData();
                            FileDownloadManager fdm;
                            if(_downloadQueue.containsKey(fileData.getProcessId())){
                                fdm = _downloadQueue.get(fileData.getProcessId());
                                if(fdm.write(fileData) == 0) {
                                    fdm.close();
                                    _downloadQueue.remove(fileData.getProcessId());
                                }
                            }else{
                                fdm = new FileDownloadManager(fileData, _uploadPath, BUF_SIZE);
                                if(fdm.isProcessing())
                                    _downloadQueue.put(fileData.getProcessId(), fdm);
                                else
                                    fdm.close();
                            }
                            if(!fdm.isProcessing()){
                                response = new ResponseData();
                                response.setSuccess(true);
                                response.setAction(request.getAction());
                                response.setMessage("Загрузка файла " + fileData.getFileName() + " завершена!");
                                response.setData(fileData.getProcessId());
                                System.out.println("Загрузка файла " + fileData.getFileName() + " завершена!");
                                ctx.writeAndFlush(response);
                            }
                        }
                        break;
                    }
//                    case DOWNLOADFILE:{
//                        if(request.getData() != null && request.getData() instanceof String) {
//                            FileManager fm = new FileManager();
//                            fm.uploadFile((String)request.getData(), ctx.channel());
//                        } else
//                            response.setMessage("Server received wrong object in body!");
//                        break;
//                    }
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

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    }
}
