package com.xinhong.ftp.util;

/**
 * Created by shijunna on 2016/6/23.
 */
//枚举类DownloadStatus代码
public enum DownloadStatus {
    Remote_File_Noexist, //远程文件不存在
//    Local_Bigger_Remote, //本地文件大于远程文件
    Download_From_Break_Success, //断点下载文件成功
//    Download_From_Break_Failed,   //断点下载文件失败
    Download_New_Success,    //全新下载文件成功
//    Download_New_Failed ,    //全新下载文件失败


    // wingsby
    Downloading,
    Downloaded,
    FAILURE;



    public static DownloadStatus fromString(String infostr) {
        if(infostr.equals("Remote_File_Noexist"))return Remote_File_Noexist;
//        if(infostr.equals("Local_Bigger_Remote"))return Local_Bigger_Remote;
        if(infostr.equals("Download_From_Break_Success"))return Download_From_Break_Success;
//        if(infostr.equals("Download_From_Break_Failed"))return Download_From_Break_Failed;
        if(infostr.equals("Download_New_Success"))return Download_New_Success;
//        if(infostr.equals("Download_New_Failed"))return Download_New_Failed;
        if(infostr.equals("Downloading"))return Downloading;
        if(infostr.equals("Downloaded"))return Downloaded;
        if(infostr.equals("FAILURE"))return FAILURE;
        return null;
    }
}
