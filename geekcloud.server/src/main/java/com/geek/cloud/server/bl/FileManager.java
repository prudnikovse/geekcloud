package com.geek.cloud.server.bl;

import com.geek.cloud.common.FileData;
import com.geek.cloud.common.FileItem;
import com.geek.cloud.common.FileList;
import com.geek.cloud.common.bl.DownloadFileManager;
import com.geek.cloud.common.bl.UploadFileManager;
import sun.dc.path.PathException;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

public class FileManager implements AutoCloseable {
    private static final int BUF_SIZE = 1024 * 1024 * 100; // 100 mb
    private String _rootPath;
    private HashMap<String, DownloadFileManager>  _downloadPool;
    private HashMap<String, UploadFileManager>  _uploadPool;

    public FileManager(String login) throws IOException{
        _rootPath = String.format("./geekcloud.server/files/%s/", login);
        if(Files.notExists(Paths.get(_rootPath)))
            createDirectory("");

        _downloadPool = new HashMap<>();
        _uploadPool = new HashMap<>();
    }

    //Записываем полученные данные в файл
    public boolean downloadFile(FileData data) throws IOException, PathException {
        DownloadFileManager fdm;
        if(_downloadPool.containsKey(data.getProcessId())){
            fdm = _downloadPool.get(data.getProcessId());
            if(fdm.write(data) == 0) {
                _downloadPool.remove(data.getProcessId());
            }
        }else{
            fdm = new DownloadFileManager(data, _rootPath, (int)data.getSize());
            if(fdm.write(data) != 0)
                _downloadPool.put(data.getProcessId(), fdm);
        }

        return fdm.isProcessing();
    }

    public FileData uploadFile(String pathFrom, String pathDirectoryTo) throws IOException {
        UploadFileManager ufm = new UploadFileManager(BUF_SIZE, _rootPath + pathFrom, pathDirectoryTo);
        FileData res = ufm.readFile();
        if(res != null)
            _uploadPool.put(res.getProcessId(), ufm);
        return res;
    }

    public FileData uploadFile(String processId) throws IOException {
        if(_uploadPool.containsKey(processId)){
            UploadFileManager ufm = _uploadPool.get(processId);
            FileData res = ufm.readFile();
            if(res == null) {
                ufm.close();
                _uploadPool.remove(processId);
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
        for(DownloadFileManager dfm : _downloadPool.values()){
            dfm.close();
        }
        _downloadPool.clear();
        for(UploadFileManager ufm : _uploadPool.values()){
            ufm.close();
        }
        _uploadPool.clear();
    }
}
