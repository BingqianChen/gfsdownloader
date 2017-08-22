package com.xinhong.gfs.download;

import com.xinhong.ftp.util.DownloadStatus;
import com.xinhong.gfs.processor.GfsParseProcess;
import com.xinhong.util.FtpConfig;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.URLDecoder;
import java.util.*;

/**
 * Created by Administrator on 2016-06-14.
 */
public class PoolFTPDownloader extends FtpDownloader {
    private final Logger logger = Logger.getLogger(PoolFTPDownloader.class);

    @Override
    public void run() {
        try {
            boolean isConnected=connect();
            String localfile=task.getLocal();
            String remotefile=task.getRemote();
            remotefile = URLDecoder.decode(remotefile,"UTF-8");
            localfile = URLDecoder.decode(localfile,"UTF-8");
            int retrycnt=0;
            while (!isConnected){
                isConnected=connect();
                Thread.sleep(500);
                if(retrycnt++>5)break;
            }
            DownloadStatus downStatus=DownloadStatus.FAILURE;
            if(isConnected)
            downStatus=download(task);
            if(downStatus.equals(DownloadStatus.FAILURE)){
                for(int i=0;i<10;i++){
                    //多次下载无法下载完成，就将其放入lazylist
                    if(!isConnected)isConnected=connect();
                    if(isConnected) {
                        downStatus = download(task);
                        if (!downStatus.equals(DownloadStatus.FAILURE)) break;
                    }
                }
            }
            if(downStatus.equals(DownloadStatus.FAILURE))
                DownloadTaskCenter.notifyDownloadStatus(task,TaskOperation.InsertLazy);
            if(isConnected)disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("下载有误");
        }
    }

    @Override
    protected void postProcess(DownloadTask task) {
        String local= task.getLocal();
        File f=new File(local);
        if(!local.endsWith(".idx")){
            startProcessGFS(local);
        }
    }

//    private void manualDownload(){
//        //检查未下载文件，调用手动下载
//        String okListPath = FtpConfig.getLocalPath() + "/" + strYMDH + "/" + strYMDH + "_ok.txt";
//        Map<String, String> filenameMap = FileHandler.getFilenameMap(okListPath);
//        //获取全部预报时效列表
//        List<String> allList = getAllList(strYMDH);
//        List<String> ymdhvList = new ArrayList<>();
//        if(filenameMap != null && filenameMap.size() > 0){
//            Iterator<String> iterator = filenameMap.keySet().iterator();
//            while (iterator.hasNext()){
//                String key = iterator.next();
//                allList.remove(key);
//                if(!filenameMap.get(key).equals("redisOK_jsybOK")){
//                    ymdhvList.add(key);
//                }
//            }
//        }
//        if(ymdhvList != null && ymdhvList.size() > 0){
//            int ymdhvListSize = ymdhvList.size();
//            int cnt = ymdhvListSize + allList.size();
//            String[] ymdhvAry = new String[cnt];
//            for (int i = 0; i < cnt; i++) {
//                if(i < ymdhvList.size()){
//                    ymdhvAry[i] = ymdhvList.get(i);
//                }else{
//                    ymdhvAry[i] = allList.get(i-ymdhvListSize);
//                }
//            }
//            DownloadManual manual = new DownloadManual();
//            manual.downloadFile(ymdhvAry);
//        }
//    }

    private List<String> getAllList(String strYMDH){
        List<String> list = new ArrayList<>();
        for (int i = 0; i < FtpConfig.getVtis().length; i++) {
            list.add(strYMDH + FtpConfig.getVtis()[i]) ;
        }
        return list;
    }
    
    public PoolFTPDownloader(String _ftpURL, String _username, String _pwd, int _ftpport, DownloadTask _task){
        //设置将过程中使用到的命令输出到控制台
        ftpURL = _ftpURL;
        username = _username;
        pwd = _pwd;
        ftpport = _ftpport;
        task=_task;
    }
    String lastLocalFile = "";
    String strYMDH = "";


    //解小文件与二进制文件
    private void startProcessGFS(String local) {
//        GfsParseProcess process = new GfsParseProcess(local);
//        new Thread(process).start();
    }
}
