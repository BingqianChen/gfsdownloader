package com.xinhong.ftp.util;

import java.util.List;

/**
 * Created by shijunna on 2016/7/12.
 */
public class ListeneObject {
    private String localPath;
    private String remotePath;
    private String logtxtPath;

    public String getLocalPath(){
        return localPath;
    }
    public String getRemotePath(){
        return remotePath;
    }
    public String getLogtxtPath(){
        return logtxtPath;
    }

    public void setLocalPath(String localPath){
        this.localPath = localPath;
    }
    public void setRemotePath(String remotePath){
        this.remotePath = remotePath;
    }
    public void setLogtxtPath(String logtxtPath){
        this.logtxtPath = logtxtPath;
    }

    private List<String> localPathList;
    private List<String> remotePathList;

    public List<String> getLocalPathList(){
        return localPathList;
    }
    public List<String> getRemotePathList(){
        return remotePathList;
    }

    public void setLocalPathList(List<String> localPathList){
        this.localPathList = localPathList;
    }
    public void setRemotePathList(List<String> remotePath){
        this.remotePathList = remotePathList;
    }

}
