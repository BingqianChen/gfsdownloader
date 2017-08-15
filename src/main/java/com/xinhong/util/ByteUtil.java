package com.xinhong.util;

/**
 * Created by shijunna on 2016/7/11.
 */
public class ByteUtil {
    public static float byte2float(byte[] b, int index) {
        int l;
        l = b[index + 0];
        l &= 0xff;
        l |= ((long) b[index + 1] << 8);
        l &= 0xffff;
        l |= ((long) b[index + 2] << 16);
        l &= 0xffffff;
        l |= ((long) b[index + 3] << 24);
        return Float.intBitsToFloat(l);
    }
    public static byte[] floatToByte(float f){
        int fbit = Float.floatToIntBits(f);
        int len = 4;
        byte[] b = new byte[len];
        for(int i=0;i<len; i++){
            b[i] = (byte)(fbit>>(24 - i * 8));
        }

        byte[] dest = new byte[len];
        System.arraycopy(b, 0, dest, 0, len);
        byte temp;
        for(int i=0;i<len/2;i++){
            temp = dest[i];
            dest[i] = dest[len - i - 1];
            dest[len - i - 1] = temp;
        }
        return dest;
    }
}
