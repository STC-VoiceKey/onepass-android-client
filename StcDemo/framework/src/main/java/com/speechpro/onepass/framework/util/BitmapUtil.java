package com.speechpro.onepass.framework.util;

import android.graphics.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static android.graphics.Bitmap.CompressFormat.JPEG;
import static android.graphics.Bitmap.createBitmap;

/**
 * @author volobuev
 * @since 29.03.16
 */
public class BitmapUtil {


    public static byte[] createJpeg(byte[] yuvData, int w, int h, int degree, int quality) throws IOException {
        Bitmap                bmp = createBitmap(yuvData, w, h, degree);
        return createJpeg(bmp, quality);
    }

    public static byte[] createJpeg(Bitmap bmp, int quality) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bmp.compress(JPEG, quality, out);
        out.flush();
        byte[] bytes = out.toByteArray();
        out.close();
        return bytes;
    }


    public static Bitmap createBitmap(byte[] yuvData, int w, int h, int degree) {
        ByteArrayOutputStream out      = new ByteArrayOutputStream();
        YuvImage              yuvImage = new YuvImage(yuvData, ImageFormat.NV21, w, h, null);
        yuvImage.compressToJpeg(new Rect(0, 0, w, h), 50, out);
        byte[] imageBytes = out.toByteArray();
        Bitmap bitmap     = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        Matrix mat        = new Matrix();
        mat.postRotate(degree);
        mat.preScale(1.0f, -1.0f);
        Bitmap res = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), mat, true);
        bitmap.recycle();
        return res;
    }
}
