package com.geek.cloud.common;

import java.io.Serializable;
import java.util.ArrayList;

public class FileList implements Serializable {
    private String _parentPath;
    private ArrayList<FileItem> _items;

    public FileList(){
        _items = new ArrayList<>();
    }

    public ArrayList<FileItem> getItems(){
        return _items;
    }

    public void addItem(FileItem item){
        _items.add(item);
    }

    public String getParentPath() {
        return _parentPath;
    }

    public void setParentPath(String parentPath) {
        _parentPath = parentPath;
    }
}
