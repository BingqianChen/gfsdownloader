package com.xinhong.gfs.processor;

import org.apache.log4j.Logger;
import ucar.grib.GribReaderUtil;
import ucar.grib.grib2.Grib2Data;
import ucar.unidata.io.RandomAccessFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by xin on 2016/6/13.
 */
public class GribTranferr {
    private final Logger logger = Logger.getLogger(GribTranferr.class);
    private static int LengthGribII = 4; //文件长度和段长所占的字节数
    private int GribVersion = 0;
    private byte[] b = new byte[1];
    //获取长度所在的字节数
    private byte[] GribByte = null;

    public static void main(String[] args) {

    }

    private RandomAccessFile raf1 = null;

    //转二进制文件
    public boolean grbFolderToTxt(String grbFolder, String txtFolder) {
        File grbFolderF = new File(grbFolder);
        if (!grbFolderF.exists())
            return false;
        File txtFolderF = new File(txtFolder);
        if (!txtFolderF.exists()) {
            txtFolderF.mkdirs();
        }

        File[] grbFiles = grbFolderF.listFiles();
        if (grbFiles == null || grbFiles.length < 1)
            return false;

        for (int i = 0; i < grbFiles.length; i++) {
            String datPath = txtFolder + "/" + grbFiles[i].getName().replace("_G_", "_A_").replace(".grb", ".dat");
            grbToTxt(grbFiles[i].getPath(), datPath);
        }
        return true;
    }

    private boolean grbToTxt(String grbPath, String datPath) {
        File grbF = new File(grbPath);
        if (!grbF.exists()) {
            //System.out.println();
            return false;
        }
        try {
            String mode = "rw"; // or "r"
            raf1 = new RandomAccessFile(grbPath, mode);

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        if (!isGribFile()) {
            String message = "GribParserByJar中" +
                    "getGribData,在GRIB文件中没有获取到【GRIB】开头，故此文件不是GRIB文件";
            //System.out.println(message);
            logger.error(message);
            return false;
        }
        try {
            while (raf1.isAtEndOfFile() == false) {
                byte[] sectGrib2 = new byte[LengthGribII];
                raf1.read(sectGrib2);
                GribVersion = GribReaderUtil.byteToInt(sectGrib2[3]);
                if (GribVersion == 1) {
                    //System.out.println("文件=GRIB1");
                    logger.info("文件=GRIB1");
                    break;
//                    return parserGrib1(filePath);
                } else if (GribVersion == 2) {
                    return parserGrib2(grbPath, datPath);
                } else {
                    String message = "com.xinhong.mids3d.datareader.parser.GribParserByJar中" +
                            "getGribData,所解出的GRIB文件既不是GRIB1也不是GRIB2，故解码错误";
                    //System.out.println(message);
                    logger.error(message);
                    raf1.close();
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            String message = "com.xinhong.mids3d.datareader.parser.GribParserByJar中" +
                    "getGribData,读取GRIB文件失败，故解码错误";
            //System.out.println(message);
            logger.error(message);
            try {
                raf1.close();
            } catch (IOException e1) {
//				e1.printStackTrace();
            }
            return false;
        }
    }


    private boolean parserGrib2(String gribPath, String datPath) {
        try {
            GribByte = new byte[8];
            raf1.read(GribByte);
            //int fileLength = GribReaderUtil.Value(GribByte, new int[]{0}, 8 * 8);
            GribByte = new byte[LengthGribII];
            while (true) {
                raf1.read(GribByte);
                int sectLength = GribReaderUtil.Value(GribByte, new int[]{0}, 8 * LengthGribII);
                raf1.read(b);
                int code = GribReaderUtil.Value(b, new int[]{0}, 8);
                if (code == 3) {
                    Grib2Data data = new Grib2Data(raf1);
                    long gg = raf1.getFilePointer();
                    data.getData1(gg - 5, 0, 0);
                    int row = data.getGds().getNy();
                    int col = data.getGds().getNx();
                    float lat1 = data.getGds().getLa1();
                    float lat2 = data.getGds().getLa2();
                    float lon1 = data.getGds().getLo1();
                    float lon2 = data.getGds().getLo2();
                    float dx = data.getGds().getDx();
                    float dy = data.getGds().getDy();

                    float[] dataAry = data.getDataAry();
                    float[] lats = new float[row];
                    float[] lons = new float[col];
                    float[][] dataAry2 = new float[row][col];
                    int sign = 1;
                    if (lat2 < lat1)
                        sign = -1 * sign;
                    for (int i = 0; i < row; i++)
                        lats[i] = lat1 + sign * i * dx;
                    for (int i = 0; i < col; i++)
                        lons[i] = lon1 + i * dy;

                    float scale = 1.0f;
                    float offset = 0.0f;
                    //// TODO: 2016/7/25 要素判断
                    File F = new File(gribPath);
                    if (F.getName().indexOf("_TT_") > -1 || F.getName().indexOf("_MXT_") > -1
                            || F.getName().indexOf("_MIT_") > -1) {
                        scale = 1.0f;
                        offset = -273.16f;
                    } else if (F.getName().indexOf("_PR_") > -1) {
                        scale = 0.01f;
                        offset = 0;
                    } else if (F.getName().indexOf("_CN_") > -1 || F.getName().indexOf("_CNL_") > -1) {
                        scale = 0.1f;
                        offset = 0;
                    }
//                    else if(F.getName().indexOf("_VV_") > -1){
//                        scale = -1f;
//                        offset = 0;
//                    }
//                    if(elemCode != null){
//                        scale = elemCode.getScale(dataType,null,null);
//                        offset = elemCode.getOffset(dataType,null,null);
//                    }
                    //将一维数据转换为二维数据
                    int index = -1; //int index = 0; 为index ++;写在调用前面，故索引开始值赋为-1
                    for (int i = 0; i < row; i++) {
                        for (int j = 0; j < col; j++) {
                            index++;
                            dataAry2[i][j] = dataAry[index] * scale + offset;
                        }
                    }
                    //二进制数据自西向东，自南向北，0~90，0~180，故全球数据行和列各取一半即可（东北半球）
                    //原始数据为90~-90 0~360

////                  按行写入txt
//                    OutputStreamWriter txtout = new OutputStreamWriter(new FileOutputStream(datPath+".txt"));
//                    txtout.write("lat:\r\n");
//                    for (int i = 0; i < lats.length; i++) {
//                        txtout.write(String.valueOf(lats[i]) + ",");
//                    }
//                    txtout.write("lon:\r\n");
//                    for (int i = 0; i < lons.length; i++) {
//                        txtout.write(String.valueOf(lons[i]) + ",");
//                    }
//                    txtout.write("data:\r\n");
//
//                    for(int i=0;i<row;i++){
//                        for(int j=0;j<col;j++){
//                            index ++;
//                            txtout.write(String.valueOf(dataAry2[i][j]) + ",");
//                        }
//                        txtout.write("\r\n");
//                    }
//                    txtout.close();

                    //按列写入txt0~90 0~180
//                    OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(datPath));
//                    for(int i=0;i<col;i++){
//                        for(int j=row - 1;j >= 0;j--){
//                            out.write(String.valueOf(dataAry2[j][i]) + ",");
//                        }
//                        out.write("\r\n");
//                    }
//                    out.close();


                    //按列写入二进制文件
//                    row = row/2 + 1;
//                    col = col/2 + 1;
                    FileOutputStream out = new FileOutputStream(new File(datPath));
                    //自西向东，自南向北，0~90，0~180
                    byte[] value = new byte[col * row * 4];
                    ByteBuffer bb = ByteBuffer.allocate(col * row * 4);

//                    for(int i=0;i<col;i++){
//                        for(int j=row - 1;j >= 0;j--){
//                            //Float 高低位转换 适应Python
//                            bb.putFloat(dataAry2[j][i]);
//                        }
//                    }
                    for (int i = row - 1; i >= 0; i--) {
                        for (int j = 0; j < col; j++) {
                            //Float 高低位转换 适应Python
                            if (j < col / 2)
                                bb.putFloat(dataAry2[i][j + col / 2]);
                            if (j >= col / 2)
                                bb.putFloat(dataAry2[i][j - col / 2]);
                        }
                    }
                    value = bb.array();
                    byte[] byte0 = new byte[4];
                    for (int i = 0; i < value.length; i = i + 4) {
                        byte0[0] = value[i + 3];
                        byte0[1] = value[i + 2];
                        byte0[2] = value[i + 1];
                        byte0[3] = value[i + 0];

                        value[i + 0] = byte0[0];
                        value[i + 1] = byte0[1];
                        value[i + 2] = byte0[2];
                        value[i + 3] = byte0[3];
                    }
                    out.write(value);
                    out.close();
                    raf1.close();
                    return true;
                } else if (code > 3) {
                    // System.out.println("当前GRIB2文件未读取到第三段，无法调用jar包进行文件解析\r\n 文件=" + gribPath);
                    logger.error("当前GRIB2文件未读取到第三段，无法调用jar包进行文件解析\r\n 文件=" + gribPath);
                    raf1.close();
                    return false;
                } else if (code > 0 && code < 3) {
                    byte[] bb = new byte[sectLength - LengthGribII - 1];
                    raf1.read(bb);
                } else {
                    //System.out.println("当前GRIB2文件段号解析错误，无法解析\r\n 文件=" + gribPath );
                    logger.error("当前GRIB2文件段号解析错误，无法解析\r\n 文件=" + gribPath);
                    raf1.close();
                    return false;
                }
            }
        } catch (Exception e) {
            String message = "com.xinhong.mids3d.datareader.parser.GribParserByJar中" +
                    "getGribData,读取GRIB文件失败，故解码错误";
            //System.out.println(message + "\r\n 文件=" + gribPath);
            logger.error(message + "\r\n 文件=" + gribPath);
            System.out.println("**************************************");
            e.printStackTrace();
            System.out.println("**************************************");
            try {
                raf1.close();
            } catch (IOException e1) {
//				e1.printStackTrace();
            }
            return false;
        }
    }


    // 判断文件是否为GRIB文件
    private boolean isGribFile() {
        boolean isGrib = false;
        try {
            while (raf1.isAtEndOfFile() == false) {
                raf1.read(b);
                char ch = GribReaderUtil.getChar(b, 0);
                if (ch == 'G') {
                    raf1.read(b);
                    ch = GribReaderUtil.getChar(b, 0);
                    if (ch == 'R') {
                        raf1.read(b);
                        ch = GribReaderUtil.getChar(b, 0);
                        if (ch == 'I') {
                            raf1.read(b);
                            ch = GribReaderUtil.getChar(b, 0);
                            if (ch == 'B') { //是GRIB文件，跳出，开始处理数据
                                isGrib = true;
                                break;
                            }//end B
                        }//end I
                    }//end R
                }//end G
            }//end while
        } catch (IOException e) {
            return false;
        }
        return isGrib;
    }


}
