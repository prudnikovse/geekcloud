package com.geek.cloud.common;

import java.io.Serializable;

public class FileItem implements Serializable {
    private boolean _isDirectory;
    private String _parentPath;
    private String _name;

    public FileItem(String parent, String name, boolean isDirectory){
        this.setParentPath(parent);
        _name = name;
        _isDirectory = isDirectory;
    }

    public String getParentPath() {
        return _parentPath;
    }

    public void setParentPath(String parentPath) {
        _parentPath = parentPath;
        if(!_parentPath.endsWith("/"))
            _parentPath += "/";
    }

    public String getName() {
        return _name;
    }

    public void setName(String name) {
        _name = name;
    }

    public boolean isDirectory() {
        return _isDirectory;
    }

    public void setIsDirectory(boolean isDirectory) {
        _isDirectory = isDirectory;
    }
}
