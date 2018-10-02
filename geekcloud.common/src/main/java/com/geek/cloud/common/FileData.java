package com.geek.cloud.common;

import java.io.Serializable;

public class FileData implements Serializable {
    private String _fileName;
    private String _filePath;
    private String _hash;
    private byte[] _data;

    //Общий размер файла
    private long totalFileSize;
    //Общее кол-во пакетов из которых состоит файл
    private long totalPacket;
    //Текущий номер пакета
    private long packet;

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

    public long getTotalFileSize() {
        return totalFileSize;
    }

    public void setTotalFileSize(long totalFileSize) {
        this.totalFileSize = totalFileSize;
    }

    public long getTotalPacket() {
        return totalPacket;
    }

    public void setTotalPacket(long packet) {
        this.totalPacket = packet;
    }

    public long getPacket() {
        return packet;
    }

    public void setPacket(long packet) {
        this.packet = packet;
    }

    public String getFileName() {
        return _fileName;
    }

    public void setFileName(String fileName) {
        _fileName = fileName;
    }

    public String getFilePath() {
        return _filePath;
    }

    public void setFilePath(String filePath) {
        _filePath = filePath;
    }
}
