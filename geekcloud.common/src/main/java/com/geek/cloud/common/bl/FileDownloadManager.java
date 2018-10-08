package com.geek.cloud.common.bl;

import com.geek.cloud.common.FileData;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

public class FileDownloadManager implements AutoCloseable {
    private ByteBuffer _buffer;
    private int _bufSize;
    private boolean _processing;

    private long _remainedPacket;
    private RandomAccessFile _raf;

    public FileDownloadManager(FileData data, String directoriPath, int bufSize) throws IOException{
        _processing = true;
        _bufSize = bufSize;
        _buffer = ByteBuffer.allocate(bufSize);
        _remainedPacket = data.getTotalPacket();

        File file = new File(directoriPath + data.getDirectoryPath() + data.getFileName());
        if(file.exists())
            file.delete();

        file.createNewFile();
        _raf = new RandomAccessFile(file, "rw");
        _raf.setLength(data.getTotalSize());

        write(data);
    }

    public long write(FileData data) throws IOException{
        int skip = (int)((data.getPacket() - 1) * _bufSize);
        _raf.seek(skip);
        _raf.write(data.getData(), 0, (int)data.getSize());

        if(--_remainedPacket == 0)
            _processing = false;

        return _remainedPacket;
    }

    public boolean isProcessing(){
        return _processing;
    }

    @Override
    public void close() throws Exception {
        if(_raf != null)
            _raf.close();
    }
}
