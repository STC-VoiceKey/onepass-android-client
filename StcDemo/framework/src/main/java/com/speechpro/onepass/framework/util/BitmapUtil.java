package com.speechpro.onepass.framework.util;

import android.graphics.*;
import android.util.Pair;

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
        Bitmap bmp = createBitmap(yuvData, w, h, degree);
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
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        YuvImage yuvImage = new YuvImage(yuvData, ImageFormat.NV21, w, h, null);
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

    public static Pair<Integer, Integer> getPictureResolution(byte[] pictureBinary) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(pictureBinary, 0, pictureBinary.length, options);
        final int width = options.outWidth;
        final int height = options.outHeight;
        return new Pair<>(width, height);
    }

    public static byte[] rotatePicture(byte[] pictureBinary, int width, int height, int degrees) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap def = BitmapFactory.decodeByteArray(pictureBinary, 0, pictureBinary.length, options);
        Matrix mtx = new Matrix();
        mtx.postRotate(degrees);
        Bitmap bm = Bitmap.createBitmap(def, 0, 0, width, height, mtx, true);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, os);
        byte[] data = os.toByteArray();
        return data;
    }

    public static byte[] resizedPicture(byte[] pictureBinary, int newWidth, int newHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bm = BitmapFactory.decodeByteArray(pictureBinary, 0, pictureBinary.length, options);

        int width = bm.getWidth();
        int height = bm.getHeight();

        if (newWidth == width && newHeight == height) {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 100, os);
            byte[] data = os.toByteArray();
            return data;
        }

        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
        byte[] resizedData = os.toByteArray();
        return resizedData;
    }
}
