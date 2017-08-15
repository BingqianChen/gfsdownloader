package com.xinhong.gfs.download;

import com.alibaba.fastjson.JSONObject;
import com.xinhong.ftp.util.DownloadStatus;
import com.xinhong.gfs.processor.GfsParseProcess;
import com.xinhong.util.FileHandler;
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
            connect();
            File file = new File(localfile);
            String txtPath = FtpConfig.getLocalPath() + "/" + strYMDH + "/" + strYMDH + ".txt";
            FileHandler.writeDownloading(file.getName(),txtPath);
            remotefile = URLDecoder.decode(remotefile,"UTF-8");
            localfile = URLDecoder.decode(localfile,"UTF-8");
            DownloadStatus downStatus = this.download(remotefile, localfile);
            if(localfile.equals(lastLocalFile)){
                String txtPath1 = FtpConfig.getLocalPath() + "/downloadFile.txt";
                FileHandler.writeDownload(strYMDH,txtPath1);
            }
            this.disconnect();
        } catch (Exception e) {
        }
    }

    @Override
    protected void postProcess(JSONObject info) {
        String local= (String) info.get("localname");
        File f=new File(local);
        String txtPath = f.getParent() + "/" + strYMDH + ".txt";
        FileHandler.writeDownload(f.getName(),txtPath);
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
    
    public PoolFTPDownloader(String _ftpURL, String _username, String _pwd, int _ftpport, String remotePath, String localPath ){
        //设置将过程中使用到的命令输出到控制台
        ftpURL = _ftpURL;
        username = _username;
        pwd = _pwd;
        ftpport = _ftpport;
        remotefile = remotePath;
        localfile = localPath;
//        logger.info("remotepath = " + remotePath);
//        logger.info("localPath = " +  localPath);
//        this.ftpClient.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));
    }
    String lastLocalFile = "";
    String strYMDH = "";

    public void setDownloadInfo(String lastLocalFile, String strYMDH){
        this.lastLocalFile = lastLocalFile;
        this.strYMDH = strYMDH;
    }

    //解小文件与二进制文件
    private void startProcessGFS(String local) {
        GfsParseProcess process = new GfsParseProcess(local);
        new Thread(process).start();
    }
}
