package com.xinhong.util;

import org.apache.log4j.Logger;

import java.io.File;

/**
 * Created by shijunna on 2017/3/15.
 */
public class deleteUtil {
    private final Logger logger = Logger.getLogger(deleteUtil.class);

    /**
     * 删除下载中断的文件并重新下载
     */
//    public void deleteFileFtp(String localfile, String remotefile) throws Exception {
//
//        File local = new File(localfile);//本地文件
//        File remote = new File(remotefile);//FTP文件
//        if (local.length() < remote.length() && local.getName().equals(remote.getName())) {
//            local.delete();
//            logger.error("已删除下载中断的文件...");
//        }
//        logger.error("重新下载该文件...");
//        try {
//            FtpdownUtil ftputil = new FtpdownUtil(localfile, remotefile);
//            ftputil.download(localfile, remotefile);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

}
