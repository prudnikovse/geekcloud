package com.geek.cloud.common.bl;

import com.geek.cloud.common.*;
import io.netty.channel.Channel;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import com.geek.cloud.common.FileData;
import com.geek.cloud.common.Helpers.HashHelper;

public class FileUploadManager {
    private ByteBuffer buffer;
    private int bufSize;

    public FileUploadManager(){
        this(9000);
    }

    public FileUploadManager(int bufSize){
        this.bufSize = bufSize;
        buffer = ByteBuffer.allocate(bufSize);
    }

//    public void uploadFile(String pathFrom, String pathDirectoryTo, Channel channel){
//        if(!channel.isOpen())
//            return;
//
//        int count;
//        int i = 1;
//        try(SeekableByteChannel fChannel = Files.newByteChannel(Paths.get(pathFrom))) {
//            int totalPackets = (int)Math.ceil(fChannel.size() * 1f / bufSize);
//            byte[] bytes = new byte[bufSize];
//
//            RequestData request = new RequestData();
//            request.setAction(Action.UPLOADFILE);
//            FileData data = new FileData();
//            data.setTotalSize(fChannel.size());
//            data.setTotalPacket(totalPackets);
//            request.setData(data);
//            do{
//                count = fChannel.read(buffer);
//                if(count != -1){
//                    buffer.flip();
//                    buffer.get(bytes);
//                    buffer.clear();
//
//                    data.setFileName(Paths.get(pathFrom).getFileName().toString());
//                    data.setDirectoryPath(pathDirectoryTo);
//                    data.setProcessId(HashHelper.getHash(data.getDirectoryPath() + data.getFileName()));
//                    data.setTotalSize(fChannel.size());
//                    data.setTotalPacket(totalPackets);
//                    data.setSize(bytes.length);
//                    data.setPacket(i++);
//                    data.setData(bytes);
//                    channel.writeAndFlush(request);
//                }
//            }while (count != -1);
//        }catch (IOException ex){
//            System.out.println(ex.getMessage());
//        }
//    }

    public ArrayList<FileData> uploadFile(String pathFrom, String pathDirectoryTo){
        ArrayList<FileData> res = new ArrayList<>();

        int count;
        int i = 1;
        try(SeekableByteChannel fChannel = Files.newByteChannel(Paths.get(pathFrom))) {
            int totalPackets = (int)Math.ceil(fChannel.size() * 1f / bufSize);
            //byte[] bytes = new byte[bufSize];

            do{
                count = fChannel.read(buffer);
                if(count != -1){
                    byte[] bytes = new byte[buffer.position()];
                    buffer.flip();
                    buffer.get(bytes);
                    buffer.clear();

                    FileData data = new FileData();
                    data.setFileName(Paths.get(pathFrom).getFileName().toString());
                    data.setDirectoryPath(pathDirectoryTo);
                    data.setProcessId(HashHelper.getHash(data.getDirectoryPath() + data.getFileName()));
                    data.setTotalSize(fChannel.size());
                    data.setTotalPacket(totalPackets);
                    data.setSize(bytes.length);
                    data.setPacket(i++);
                    data.setData(bytes);
                    res.add(data);
                }
            }while (count != -1);
        }catch (IOException ex){
            System.out.println(ex.getMessage());
        }
        return res;
    }
}
