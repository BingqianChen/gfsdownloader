package com.xinhong.gfs.download;

import com.xinhong.util.FtpConfig;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by shijunna on 2016/8/9.
 * 手动补下文件，补发消息
 */
public class DownloadManual {
    private final Logger logger = Logger.getLogger(DownloadManual.class);
    static {
        init();
        FtpConfig.init();
    }
    static Properties pro = null;

    public static void init() {
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            if(classLoader.getResourceAsStream("gfsConf/log4j.properties")==null){
                PropertyConfigurator.configure(new FileInputStream(new File("gfsConf/log4j.properties")) );
                pro = new Properties();
                pro.load(new FileInputStream(new File("gfsConf/log4j.properties")));
            }else{
                PropertyConfigurator.configure(classLoader.getResourceAsStream("gfsConf/log4j.properties"));
                pro = new Properties();
                pro.load(classLoader.getResourceAsStream("gfsConf/log4j.properties"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        DownloadManual manual = new DownloadManual();

//        args = new String[]{"2016103112013"};
        manual.downloadFile(args);
//        if(args == null || args.length < 1){
//            System.out.println("未正确设置参数");
//        }
//        for (int i = 0; i < args.length; i++) {
//            manual.downloadFile(new String[]{args[i]});
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                System.out.println("sleep 1000 出错");
//                e.printStackTrace();
//            }
//        }

    }

    public void downloadFile(String[] args){
//        args = new String[]{"2016080700006"};
        if(args == null || args.length < 1){
            logger.error("所输入的参数==null,或长度<1");
        }
        logger.info("开启手动下载线程池");
        initThreadPool();
        for(int i=0;i<args.length;i++) {
            String ymdhvti = args[i];
            if(ymdhvti == null || ymdhvti.trim().length() != 13){
                logger.error("第" + i + "个参数输入错误吃，为空或者长度！=13");
                continue;
            }else{
                String strYMDH = ymdhvti.substring(0,10);
                String strHH = strYMDH.substring(8,10);
                String strVTI = ymdhvti.substring(10);
                String gfsname = FtpConfig.getExt() + "t" + strHH + FtpConfig.getScale() + strVTI;
                String remotepath = FtpConfig.getPath() + "/" + FtpConfig.getExt() + strYMDH + "/" + gfsname;
                String localpath = FtpConfig.getLocalPath() + "/" + strYMDH + "/" + strYMDH + "_" + gfsname;
                String remoteidx = remotepath + ".idx";
                String localidx = localpath + ".idx";
                //最后文件名称输入abc,确保不会修改正常下载的文件列表
                newThreadDownload(remoteidx,localidx,"abc",strYMDH);
                newThreadDownload(remotepath,localpath,"abc",strYMDH);
            }
        }
        logger.info("关闭手动下载线程池");
        shutdownPool();
    }
    private ExecutorService pool = null;
    /**
     * 初始化线程池 固定大小
     */
    private void initThreadPool() {
        pool = Executors.newFixedThreadPool(FtpConfig.getThreads());
    }
    private void newThreadDownload(String rem_file, String local_file, String lastLocalFile,String StrYMDH) {
        PoolFTPDownloader ftpDownloader = new PoolFTPDownloader(FtpConfig.getHOST(),
                FtpConfig.getUsername(), FtpConfig.getPath(), FtpConfig.getPORT(),
                rem_file, local_file);
        ftpDownloader.setDownloadInfo(lastLocalFile,StrYMDH);
        pool.execute(ftpDownloader);
    }
    /**
     * 关闭线程池
     */
    private void shutdownPool() {
        pool.shutdown();
    }


}
