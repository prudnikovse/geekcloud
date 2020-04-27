package com.geek.cloud.common.bl;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import com.geek.cloud.common.FileData;
import com.geek.cloud.common.Helpers.HashHelper;

public class UploadFileManager implements AutoCloseable {
    private int _bufSize;
    private ByteBuffer _buffer;
    private byte[] _bytes;
    private String _processId;
    private String _fileName;
    private String _pathDirectoryTo;
    private int _totalPackets;
    private int _currentPacket = 0;
    private SeekableByteChannel _channel;

    public UploadFileManager(int bufSize){
        _bufSize = bufSize;
        _buffer = ByteBuffer.allocate(bufSize);
        _bytes = new byte[bufSize];
    }

    public UploadFileManager(int bufSize, String pathFrom, String pathDirectoryTo) throws IOException{
        this(bufSize);
        _channel = Files.newByteChannel(Paths.get(pathFrom));
        _totalPackets = (int)Math.ceil(_channel.size() * 1f / bufSize);
        _fileName = Paths.get(pathFrom).getFileName().toString();
        _pathDirectoryTo = pathDirectoryTo;
        _processId = HashHelper.getHash(pathDirectoryTo + _fileName);
    }

    public FileData readFile() throws IOException{
        return readFile(++_currentPacket);
    }

    public FileData readFile(int packet) throws IOException{
        if(packet <= 0 || packet > _totalPackets)
            return null;

        _channel.position(_bufSize * (packet - 1));
        int byteCount = _channel.read(_buffer);
        _buffer.flip();
        _buffer.get(_bytes,0, byteCount);
        _buffer.clear();

        FileData res = new FileData();
        res.setTotalSize(_channel.size());
        res.setTotalPacket(_totalPackets);
        res.setFileName(_fileName);
        res.setDirectoryPath(_pathDirectoryTo);
        res.setProcessId(_processId);
        res.setSize(byteCount);
        res.setPacket(packet);
        res.setData(_bytes);

        return res;
    }

    public void reset(){
        _currentPacket = 0;
    }

    public ArrayList<FileData> uploadFile(String pathFrom, String pathDirectoryTo){
        ArrayList<FileData> res = new ArrayList<>();

        int count;
        int i = 1;
        try(SeekableByteChannel fChannel = Files.newByteChannel(Paths.get(pathFrom))) {
            int totalPackets = (int)Math.ceil(fChannel.size() * 1f / _bufSize);
            //byte[] bytes = new byte[bufSize];

            do{
                count = fChannel.read(_buffer);
                if(count != -1){
                    byte[] bytes = new byte[_buffer.position()];
                    _buffer.flip();
                    _buffer.get(bytes);
                    _buffer.clear();

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

    public String getProcessId() {
        return _processId;
    }

    @Override
    public void close() {
        if(_channel != null && _channel.isOpen())
            try {
                _channel.close();
            }catch (IOException ex){
                ex.printStackTrace();
            }
    }
}
