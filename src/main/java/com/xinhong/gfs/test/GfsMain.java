package com.xinhong.gfs.test;

import com.xinhong.gfs.processor.FliterListener;
import com.xinhong.gfs.processor.GfsParseProcess;
import com.xinhong.gfs.processor.GribTranferr;
import com.xinhong.util.ConfigCommon;
import com.xinhong.util.ConfigUtil;
import com.xinhong.util.FtpConfig;

import java.io.File;
import java.io.IOException;

/**
 * Created by xin on 2016/6/16.
 */
public class GfsMain {
    static {
        FtpConfig.init();
    }
    public static void main(String[] args) {
//        GfsMain gfsMain = new GfsMain();
//        gfsMain.testJSYB(args);

//        for(int i=0;i<args.length;i++){
//            GfsMain gfsMain = new GfsMain();
//            gfsMain.gfsDisposeByPath(args[i] );
//        }
        String path = "E:\\download\\gfsdata\\2016103112";
        GfsMain gfsMain = new GfsMain();
        gfsMain.gfsDisposeByPath(path);
//        String path = "E:\\download\\gfsdata\\2016090512\\2016090512_gfs.t12z.pgrb2.0p25.f013";
//        File f = new File(path);
//        System.out.println(f.lastModified());
//        System.out.println("s");

//        GfsMain gfsMain = new GfsMain();
////        args = new String[]{"E:\\download\\gfsdata\\2016080312\\gfs.t12z.pgrb2.0p25.f096_folder"};
//        gfsMain.gfsDispose2Dat(args);

    }

    private void testJSYB(String[] ymdhvtiAry){
//        ymdhvtiAry = new String[]{"2016080300006"};
        for(int i=0;i<ymdhvtiAry.length;i++) {

            System.out.println("args[" + i + "]=" + ymdhvtiAry[i]);
            String strYMDH = ymdhvtiAry[i].substring(0,10);
            String strVTI = ymdhvtiAry[i].substring(10);
            String path = ConfigUtil.getProperty(ConfigCommon.JSYB_OUTFILE_PATH);
            Process ps = null;
//            String[] cmdAry = new String[]{"/bin/sh","-c","/xinhong/gfsdownload2jsyb/jsyb/source/jsyb.out",strYMDH,strVTI};
            try {
                String cmd = path + " " + strYMDH + " " + strVTI;
                System.out.println("执行命令：" + cmd);
//                for (String s : cmdAry) {
//                    System.out.println(s);
//                }
                System.out.println("开始执行：ps = Runtime.getRuntime().exec(cmdAry);");
                ps = Runtime.getRuntime().exec(cmd);
                try {
                    Thread.sleep(60 * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                String sOut = "";
                StackTraceElement[] trace = e.getStackTrace();
                for (StackTraceElement s : trace) {
                    sOut += "\t at " + s + "\r\n";
                }
                System.out.println(sOut);
                continue;
            }

        }

    }

    private void gfsDisposeByPath(String datePath){
        int cnt = 0;
        int num = 10;
        File f = new File(datePath);
        if(f.exists()){
            File[] listFiles = f.listFiles();
            for(int i=0;i<listFiles.length;i++){
                System.out.println("开始处理 " +listFiles[i].getName());
                if(!listFiles[i].isDirectory() &&
                        listFiles[i].getName().indexOf(".idx") < 0 &&
                        listFiles[i].getName().indexOf(".txt") < 0){
                    System.out.println("开始处理 " +listFiles[i].getName());
                    cnt ++;
                    GfsParseProcess fliter = new GfsParseProcess(listFiles[i].getPath());
                    Thread run = new Thread(fliter);
                    run.setName("gfsTest");
                    run.start();
                }else{
                    System.out.println("目錄不處理 " +listFiles[i].getName());
                }
            }

        }
    }

    private void gfsDispose2Dat(String[] gfsFolders) {
        for (String gfsFolder : gfsFolders) {//循环YMDH
            System.out.println("开始处理" + gfsFolder);
            GribTranferr tranferr = new GribTranferr();
            String gfsSmallBinFolder = gfsFolder.replace("_folder", "_binSwapFolder");
            tranferr.grbFolderToTxt(gfsFolder, gfsSmallBinFolder);

            try {
                Thread.sleep(1000 * 120);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}


