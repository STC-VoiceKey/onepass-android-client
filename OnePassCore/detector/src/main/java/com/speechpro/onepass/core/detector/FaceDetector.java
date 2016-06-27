package com.speechpro.onepass.core.detector;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

/**
 * @author volobuev
 * @since 27.01.2016
 */
public class FaceDetector implements IFaceDetector {

    private static final int    MAX_FACES = 5;
    private static final String TAG       = "FaceDetector";

    private int                               imageWidth;
    private int                               imageHeight;
    private android.media.FaceDetector        detector;
    private android.media.FaceDetector.Face[] faces;


    @Override
    public boolean isFaceConform(byte[] faceData) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap bitmap = BitmapFactory.decodeByteArray(faceData, 0, faceData.length, options);
        return isFaceConform(bitmap);
    }

    @Override
    public boolean isFaceConform(Bitmap bitmap) {
        imageWidth = bitmap.getWidth();
        imageHeight = bitmap.getHeight();
        Log.d(TAG, "Image width = " + imageWidth + "; height = " + imageHeight);

        faces = new android.media.FaceDetector.Face[MAX_FACES];
        detector = new android.media.FaceDetector(imageWidth, imageHeight, MAX_FACES);

        int faceDetected = detector.findFaces(bitmap, faces);
        Log.d(TAG, "faceDetected = " + faceDetected);
        if (faceDetected != 1) {
            return false;
        }

        android.media.FaceDetector.Face face = faces[0];
        Log.d(TAG, "face = " + face);

        if (face == null) {
            return false;
        }
        // Must be one face, confidence is recommended more than 0.4
        // and proportion between eyes distance and image width must be 1/3
        boolean confidence = face.confidence() > 0.4;
        Log.d(TAG, "face.confidence() > 0.4 = " + confidence);
        boolean distance = 3.5 * face.eyesDistance() > imageWidth;
        Log.d(TAG, "3 * face.eyesDistance() > imageWidth = " + distance);
        return confidence && distance;
    }
}
