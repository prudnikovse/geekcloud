package com.geek.cloud.common.bl;

import com.geek.cloud.common.FileData;
import com.geek.cloud.common.FileItem;
import com.geek.cloud.common.FileList;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import sun.dc.path.PathException;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

public class FileHandler implements AutoCloseable {
    private static final int BUF_SIZE = 1024 * 10; // 10 mb
    private String _rootPath;
    //private String _login;
    private HashMap<String, DownloadFileManager>  _downloadQueue;
    private HashMap<String, UploadFileManager>  _uploadQueue;

    public FileHandler(String login) throws IOException{
        //_login = login;
        _rootPath = String.format("./geekcloud.server/files/%s/", login);
        if(Files.notExists(Paths.get(_rootPath)))
            createDirectory("");

        _downloadQueue = new HashMap<>();
        _uploadQueue = new HashMap<>();
    }

    public boolean uploadFile(FileData data) throws IOException, PathException {
        DownloadFileManager fdm;
        if(_downloadQueue.containsKey(data.getProcessId())){
            fdm = _downloadQueue.get(data.getProcessId());
            if(fdm.write(data) == 0) {
                _downloadQueue.remove(data.getProcessId());
            }
        }else{
            fdm = new DownloadFileManager(data, _rootPath, (int)data.getSize());
            if(fdm.write(data) != 0)
                _downloadQueue.put(data.getProcessId(), fdm);
        }

        return fdm.isProcessing();
    }

    public FileData downloadFile(String pathFrom, String pathDirectoryTo) throws IOException {
        UploadFileManager ufm = new UploadFileManager(BUF_SIZE, _rootPath + pathFrom, pathDirectoryTo);
        FileData res = ufm.readFile();
        if(res != null)
            _uploadQueue.put(res.getProcessId(), ufm);
        return res;
    }

    public FileData downloadFile(String processId) throws IOException {
        if(_uploadQueue.containsKey(processId)){
            UploadFileManager ufm = _uploadQueue.get(processId);
            FileData res = ufm.readFile();
            if(res == null) {
                ufm.close();
                _uploadQueue.remove(processId);
            }
            return res;
        }

        return null;
    }

    public boolean deleteFile(String sPath) throws IOException {
        Path path = Paths.get(_rootPath  + sPath);
        return deleteFile(path);
    }

    public boolean deleteFile(Path path) throws IOException {
        if(Files.isDirectory(path)){
            try(DirectoryStream<Path> dirstrm = Files.newDirectoryStream(path)){
                for (Path entry : dirstrm){
                    deleteFile(entry);
                }
            }
        }
        return Files.deleteIfExists(path);
    }

    public boolean renameFile(String path, String newFileName) {
        boolean res;
        File oFile = new File(String.format("%s%s/", _rootPath, path ));
        if(res = oFile.exists()) {
            File nFile = new File(String.format("%s/%s/", Paths.get(_rootPath + path).getParent().toString(),
                    newFileName));
            res = oFile.renameTo(nFile);
        }
        return res;
    }

    public void createDirectory(String path) throws IOException{
        Files.createDirectory(Paths.get(_rootPath + path));
    }

    public FileList getFileList(String sPath) throws IOException{
        Path path = Paths.get(_rootPath + sPath);
        if(!Files.isDirectory(path))
            return null;

        FileList res = new FileList();
        try(DirectoryStream<Path> dirstrm = Files.newDirectoryStream(path)){
            for (Path entry : dirstrm){
                res.addItem(new FileItem(sPath, entry.getFileName().toString(), Files.isDirectory(entry)));
            }
        }
        return res;
    }

    @Override
    public void close() {
        for(DownloadFileManager fdm : _downloadQueue.values()){
            fdm.close();
        }
    }
}
