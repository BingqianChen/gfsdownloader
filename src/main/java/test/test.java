package test;

import com.xinhong.gfs.processor.ElemLevel;

import java.net.URL;

/**
 * Created by wingsby on 2017/8/21.
 */
public class test {
    public static void main(String[] args) {
        URL A=ElemLevel.class.getClassLoader().getResource("");
        URL B=ElemLevel.class.getClassLoader().getResource("/");
        URL c=ElemLevel.class.getClassLoader().getResource("/gfsConf/jsybElem.dat");
        URL cs=ElemLevel.class.getClassLoader().getResource("gfsConf/jsybElem.dat");
        System.out.println(A.getFile());

        ElemLevel.getElemMap();
        System.out.println();
    }
}
