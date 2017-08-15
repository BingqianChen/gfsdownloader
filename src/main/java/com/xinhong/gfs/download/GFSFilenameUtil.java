package com.xinhong.gfs.download;

import com.xinhong.ftp.util.FTPUtil;
import com.xinhong.util.DateUtil;
import com.xinhong.util.FtpConfig;
import org.apache.commons.net.ftp.FTPClient;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wingsby on 2017/8/14.
 */
public class GFSFilenameUtil {

    public static List<DownloadTask> getRemoteList(String strYMDH) {
        String strHH = strYMDH.substring(8, 10);
        List<DownloadTask> remoteFileList = new ArrayList<>(FtpConfig.getVtis().length * 2);
        for (int i = 0; i < FtpConfig.getVtis().length; i++) {
            String gfsName = FtpConfig.getExt() + "t" + strHH + FtpConfig.getScale() + FtpConfig.getVtis()[i];
            String gfgSubpath = "/" + FtpConfig.getExt() + strYMDH + "/" + gfsName;
            String remotePath = FtpConfig.getPath() + gfgSubpath;
            String remoteIdxPath = remotePath + ".idx";
            String localpath = FtpConfig.getLocalPath() + "/" + strYMDH + "/" + strYMDH + "_" + gfsName;
            String localIdxpath = localpath + ".idx";
            remoteFileList.add(new DownloadTask(remotePath,localpath,strYMDH.trim()+FtpConfig.getVtis()[i].trim(),null));
            remoteFileList.add(new DownloadTask(remoteIdxPath,localIdxpath,strYMDH.trim()+FtpConfig.getVtis()[i].trim(),null));
        }
        return remoteFileList;
    }


//    private TreeMap<String, String> getFilemapList(String strYMDH) {
//        String strHH = strYMDH.substring(8, 10);
//        String txtPath = FtpConfig.getLocalPath() + "/" + strYMDH + "/" + strYMDH + ".txt";
//        Map<String, String> filenameMap = FileHandler.getFilenameMap(txtPath);
//        TreeMap<String, String> filepathTreeMap = new TreeMap<String, String>();
//        for (int i = 0; i < FtpConfig.getVtis().length; i++) {
//            String remoteGfsName = FtpConfig.getExt() + "t" + strHH + FtpConfig.getScale() + FtpConfig.getVtis()[i];
//            String remoteSubpath = "/" + FtpConfig.getExt() + strYMDH + "/" + remoteGfsName;
//            String remotePath = FtpConfig.getPath() + remoteSubpath;
//            String localGfsName = strYMDH + "_" + FtpConfig.getExt() + "t" + strHH + FtpConfig.getScale() + FtpConfig.getVtis()[i];
//            String localpath = FtpConfig.getLocalPath() + "/" + strYMDH + "/" + localGfsName;
//            if (filenameMap == null || !filenameMap.containsKey(localGfsName + ".idx")
//                    || filenameMap.get(localGfsName + ".idx").equals(FileHandler.downloading)) {
//                filepathTreeMap.put(remotePath + ".idx", localpath + ".idx");
//            }
//            if (filenameMap == null || !filenameMap.containsKey(localGfsName)
//                    || filenameMap.get(localGfsName).equals(FileHandler.downloading)) {
//                filepathTreeMap.put(remotePath, localpath);
//            }
//            if (i == FtpConfig.getVtis().length - 1) {
//                lastLocal = localpath;
//            }
//        }
//        return filepathTreeMap;
//    }
//
//    private TreeMap<List<String>, List<String>> getFilepathList(String strYMDH) {
//        String strHH = strYMDH.substring(8, 10);
//        String txtPath = FtpConfig.getLocalPath() + "/" + strYMDH + "/" + strYMDH + ".txt";
//        Map<String, String> filenameMap = FileHandler.getFilenameMap(txtPath);
//        TreeMap<List<String>, List<String>> map = new TreeMap<>();
//        List<String> remoteList = new ArrayList<>();
//        List<String> localList = new ArrayList<>();
//        for (int i = 0; i < FtpConfig.getVtis().length; i++) {
//            String remoteGfsName = FtpConfig.getExt() + "t" + strHH + FtpConfig.getScale() + FtpConfig.getVtis()[i];
//            String remoteSubpath = "/" + FtpConfig.getExt() + strYMDH + "/" + remoteGfsName;
//            String remotePath = FtpConfig.getPath() + remoteSubpath;
//            String localGfsName = strYMDH + "_" + FtpConfig.getExt() + "t" + strHH + FtpConfig.getScale() + FtpConfig.getVtis()[i];
//            String localpath = FtpConfig.getLocalPath() + "/" + strYMDH + "/" + localGfsName;
//            if (filenameMap == null || !filenameMap.containsKey(localGfsName + ".idx")
//                    || filenameMap.get(locTaskalGfsName + ".idx").equals(FileHandler.downloading)) {
//                remoteList.add(remotePath + ".idx");
//                localList.add(localpath + ".idx");
//            }
//            if (filenameMap == null || !filenameMap.containsKey(localGfsName)
//                    || filenameMap.get(localGfsName).equals(FileHandler.downloading)) {
//                remoteList.add(remotePath);
//                localList.add(localpath);
//            }
//            if (i == FtpConfig.getVtis().length - 1) {
//                lastLocal = localpath;
//            }
//        }
//        map.put(remoteList, localList);
//        return map;
//    }


    public static String getLocalFilePath(String strYMDH,String gfsname) {
        String localpath = FtpConfig.getLocalPath() + "/" + strYMDH + "/" + strYMDH + "_" + gfsname;
        return localpath;
    }


    public static  List<DownloadTask>  getCurrent() {
        String currdateDate = DateUtil.getCurrentDate();
        String fileDate = DateUtil.dateAddHour(currdateDate, -8);
//        logger.info("当前系统时间-8小时：" + fileDate);
        String[] dateAry = fileDate.split(" ");
        String[] ymd = dateAry[0].split("-");
        String year = ymd[0];
        String month = ymd[1];
        String day = ymd[2];
        String hour = dateAry[1];
        String HH = getHH(hour);
        String currymdh = year + month + day + hour;
        if (HH.equals("12") && Integer.parseInt(hour) < 5) {
            fileDate = DateUtil.dateAddHour(fileDate, -24);
            dateAry = fileDate.split(" ");
            ymd = dateAry[0].split("-");
            year = ymd[0];
            month = ymd[1];
            day = ymd[2];
        }
        String ymdh = year + month + day + HH;
        FTPClient client = FTPUtil.getConnectedFTPClient(FtpConfig.getHOST(), FtpConfig.getPORT(),
                FtpConfig.getUsername(), FtpConfig.getPassword());
        List<DownloadTask> remoteList = null;
        if (client != null) {
            String remoteDir = FtpConfig.getPath() + "/" + FtpConfig.getExt() + ymdh;
            String remoteFile = "";
            remoteList = GFSFilenameUtil.getRemoteList(ymdh);
        }

        return remoteList;
    }

    private static String getHH(String hour) {
        String HH = null;
        int ihour=Integer.valueOf(hour);
        if(ihour<16&&ihour>3)return "00";
        else if((ihour>=16&&ihour<24)||(ihour>=0&&ihour<4))return "12";
        return HH;
    }
}
