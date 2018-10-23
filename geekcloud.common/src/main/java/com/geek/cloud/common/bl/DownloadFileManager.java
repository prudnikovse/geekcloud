package com.geek.cloud.common.bl;

import com.geek.cloud.common.FileData;
import sun.dc.path.PathException;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;

public class DownloadFileManager implements AutoCloseable {
    private int _bufSize;
    private ByteBuffer _buffer;
    private boolean _processing;
    private long _remainedPacket;
    private RandomAccessFile _raf;
    private File tmpFile;
    private String _path;

    public DownloadFileManager(FileData data, String directoryPath, int bufSize) throws IOException, PathException{
        _processing = true;
        _bufSize = bufSize;
        _buffer = ByteBuffer.allocate(bufSize);
        _remainedPacket = data.getTotalPacket();

        tmpFile = new File(directoryPath + data.getDirectoryPath() + data.getProcessId());
        _path = directoryPath + data.getDirectoryPath() + data.getFileName();
        //_path = Paths.get(directoriPath + data.getDirectoryPath() + data.getFileName());

        if(Files.exists(Paths.get(_path)) || tmpFile.exists())
            throw new PathException();

//        File file = new File(directoriPath + data.getDirectoryPath() + data.getFileName());
//        if(file.exists())
//            file.delete();
//        file.createNewFile();

        tmpFile.createNewFile();

        _raf = new RandomAccessFile(tmpFile, "rw");
        _raf.setLength(data.getTotalSize());

        //write(data);
    }

    public long write(FileData data) throws IOException{
        int skip = (int)((data.getPacket() - 1) * _bufSize);
        _raf.seek(skip);
        _raf.write(data.getData(), 0, (int)data.getSize());

        if(--_remainedPacket == 0) {
            _raf.close();
            File file = new File(_path);
            tmpFile.renameTo(file);
            _processing = false;
        }

        return _remainedPacket;
    }

    public boolean isProcessing(){
        return _processing;
    }

    @Override
    public void close() {
        if(_raf != null)
            try {
                _raf.close();
            }catch (IOException ex){
                ex.printStackTrace();
            }
    }
}
