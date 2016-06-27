package com.speechpro.onepass.core.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * @author volobuev
 * @since 01.12.2015
 */
public final class Util {

    public static short[] getShorts(byte[] bytes) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN);
        short[] shorts = new short[bytes.length / 2];
        int pos = 0;
        while (pos < byteBuffer.limit()) {
            shorts[pos / 2] = byteBuffer.getShort();
            pos += 2;
        }
        return shorts;
    }

    public static Bitmap convert(byte[] faceData){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        return BitmapFactory.decodeByteArray(faceData, 0, faceData.length, options);
    }
}
