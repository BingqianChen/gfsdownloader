package test;

import com.xinhong.gfs.processor.ElemLevel;
import com.xinhong.gfs.processor.GribTranferr;
import com.xinhong.util.ConfigCommon;
import com.xinhong.util.ConfigUtil;

import java.io.*;

/**
 * Created by wingsby on 2017/8/21.
 */
public class ManualGrib2binary {

    private static String smallFolderSign = "_folder";
    private static String smallBinFolderSign = "_binSwapFolder";

    private static String gfsSmallFolder = "";
    private static String gfsSmallBinFolder = "";
    static String strYMDH;
    static String vti;

    public static void main(String[] args) {
        String filename = null;
        if (args != null) {
            filename = args[0];
        }

        name(filename);
    }


    public static void name(String path) {
        String idxPath = "";
        String gfsPath = "";
        if (path.lastIndexOf(".idx") > -1) {
            idxPath = path;
            gfsPath = idxPath.replace(".idx", "");
        } else {
            idxPath = path + ".idx";
            gfsPath = path;
        }
        File idxF = new File(idxPath);
        File gfsF = new File(gfsPath);
        gfsPath = gfsPath.replace("\\", "/");
        int idx0 = gfsPath.lastIndexOf("/");
        strYMDH = gfsPath.substring(0, idx0);
        idx0 = strYMDH.lastIndexOf("/");
        strYMDH = strYMDH.substring(idx0 + 1, strYMDH.length());
        int idx = gfsPath.lastIndexOf(".");
        vti = gfsPath.substring(idx + 2, idx + 5);
        if (idxF.exists() && gfsF.exists())
            if (ConfigUtil.getBoolean(ConfigCommon.JSYB_CREATE)) {
                fliterGfs(idxF, gfsF);
//                logger.info("文件解析完成，开始转换成二进制dat文件，file = " + path);
                gfsSmallBinFolder = gfsSmallFolder.replace(smallFolderSign, smallBinFolderSign);
                GribTranferr tranfer = new GribTranferr();
                boolean bl = tranfer.grbFolderToTxt(gfsSmallFolder, gfsSmallBinFolder);

            }
    }



    private static boolean fliterGfs(File idxF, File gfsF){
        try {
            InputStream input = new FileInputStream(gfsF);
            BufferedReader reader = new BufferedReader(new FileReader(idxF));
            int fileIndex = 0; String filename = "";
            int start = 0,end = 0;
            String s;
            while((s = reader.readLine()) != null){
                int idx = s.indexOf(":");
                if(idx > 0){
                    fileIndex  = Integer.parseInt(s.substring(0,idx));
                }
                int idx1 = s.indexOf(":",fileIndex + 1);
                if(idx1 > 0){
                    start = Integer.parseInt(s.substring(idx + 1,idx1));
                }
                //编辑小文件名 XHGFS_HH_G_2016071500024_0500.grb
                filename = getXHFilename(s);
                break;

            }

            while((s = reader.readLine()) != null){
                int idx = s.indexOf(":");
                if(idx > 0){
                    fileIndex  = Integer.parseInt(s.substring(0,idx));
                }
//                if(fileIndex == 10){
//                    System.out.println(fileIndex);
//                }
                int idx1 = s.indexOf(":",idx + 1);
                if(idx1 > 0){
                    end = Integer.parseInt(s.substring(idx + 1,idx1));
                }
                if(filename != null && filename.length() > 0){
                    gfsSmallFolder = gfsF.getPath() + smallFolderSign;
                    String smallPath = gfsSmallFolder + "/" + filename;
                    File smallFile = new File(smallPath);
                    if(!smallFile.exists()){
                        if(!smallFile.getParentFile().exists()){
                            smallFile.getParentFile().mkdirs();
                        }
                        smallFile.createNewFile();
                    }
                    OutputStream searchOsw = new FileOutputStream(smallFile);
                    byte[] data = new byte[end - start];
                    input.read(data);
                    searchOsw.write(data);
                    searchOsw.close();
                }else{
                    input.skip(end - start);
                }
                filename = getXHFilename(s);
                start = end;
            }
            //最后一个文件
            if(filename != null && filename.length() > 0) {
                String smallPath = gfsF.getPath() + "_folder/" + filename;
                File smallFile = new File(smallPath);
                if (!smallFile.exists()) {
                    if (!smallFile.getParentFile().exists()) {
                        smallFile.getParentFile().mkdirs();
                    }
                    smallFile.createNewFile();
                }
                OutputStream searchOsw = new FileOutputStream(smallFile);
                byte[] data = new byte[(int) (gfsF.length() - start)];
                input.read(data);
                searchOsw.write(data);
                searchOsw.close();
                input.close();
                reader.close();
            }
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }


    private static String getXHFilename(String s){
        if(s == null || s.length() < 0)
            return null;
        String[] ary = s.split(":");
        if(ary.length < 5){
//            logger.error("所输入的idx文件中的文件名格式不正确，s=" + s);
            return null;
        }
        String elem = ary[3].trim().toUpperCase();
        String level = ary[4].trim().replace(" ","").toLowerCase();
        String key = elem + "_" + level;
        if(ElemLevel.getElemMap().containsKey(key)) {
            String filename = "XHGFS_G_" +
                    ElemLevel.getElemMap().get(key) +
                    "_" + strYMDH + vti + ".grb";
            return filename;
        }else{
//            logger.info("所输入的idx文件中的文件名要素和层次不在配置之内，s=" + s);
            return null;
        }
    }
}
