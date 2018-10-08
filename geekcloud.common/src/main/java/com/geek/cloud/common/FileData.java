package com.geek.cloud.common;

import java.io.Serializable;

public class FileData implements Serializable {
    private String _fileName;
    private String _directoryPath;
    private String _hash;
    private String _processId;

    public String getProcessId() {
        return _processId;
    }

    public void setProcessId(String processId) {
        _processId = processId;
    }

    private byte[] _data;

    //Общий размер файла
    private long _totalSize;
    //Текущий размер
    private long _size;
    //Общее кол-во пакетов из которых состоит файл
    private long _totalPacket;
    //Текущий номер пакета
    private long _packet;

    public String getHash() {
        return _hash;
    }

    public void setHash(String hash) {
        _hash = hash;
    }

    public byte[] getData() {
        return _data;
    }

    public void setData(byte[] data) {
        _data = data;
    }

    public long getSize() {
        return _size;
    }

    public void setSize(long size) {
        _size = size;
    }

    public long getTotalSize() {
        return _totalSize;
    }

    public void setTotalSize(long totalSize) {
        _totalSize = totalSize;
    }

    public long getTotalPacket() {
        return _totalPacket;
    }

    public void setTotalPacket(long packet) {
        _totalPacket = packet;
    }

    public long getPacket() {
        return _packet;
    }

    public void setPacket(long packet) {
        _packet = packet;
    }

    public String getFileName() {
        return _fileName;
    }

    public void setFileName(String fileName) {
        _fileName = fileName;
    }

    public String getDirectoryPath() {
        return _directoryPath;
    }

    public void setDirectoryPath(String directoryPath) {
        _directoryPath = directoryPath;
    }
}
