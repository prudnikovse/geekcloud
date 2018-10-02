package com.geek.cloud.server.bl;

import java.io.IOException;
import java.nio.*;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import com.geek.cloud.common.*;

public class FileManager {
    private ByteBuffer buffer;
    private int bufSize;

    public FileManager(){
        this(9000);
    }

    public FileManager(int bufSize){
        this.bufSize = bufSize;
        buffer = ByteBuffer.allocate(bufSize);
    }

    public ArrayList<FileData> readFile(String path){
        ArrayList<FileData> res = new ArrayList<>();
        Path filePath = null;
        int count;
        int i = 1;
        try(SeekableByteChannel fChannel = Files.newByteChannel(Paths.get(path))) {
            int totalPackets = (int)Math.ceil(fChannel.size() * 1f / bufSize);
            do{
                count = fChannel.read(buffer);
                if(count != -1){
                    byte[] bytes = new byte[buffer.position()];
                    buffer.flip();
                    buffer.get(bytes);
                    buffer.clear();

                    FileData data = new FileData();
                    data.setTotalFileSize(fChannel.size());
                    data.setTotalPacket(totalPackets);
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
