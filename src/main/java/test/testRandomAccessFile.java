package test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

/**
 * Created by wingsby on 2017/8/17.
 */
public class testRandomAccessFile {
    public static void main(String[] args) {
        int[]arr=new int[100000];
        try {
            RandomAccessFile file=new RandomAccessFile("d:/test.dat","rw");
            ByteBuffer buffer=ByteBuffer.allocate(1000*4);

            for(int i=0;i<100000;i++){
                arr[i]=i;
                buffer.putInt(i);
                if(i%1000==0){
                    file.write(buffer.array());

                    buffer.clear();
                }
            }
            file.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
