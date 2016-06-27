package com.speechpro.onepass.core.detector;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.util.Log;
import android.util.SparseArray;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.speechpro.onepass.core.utils.Util;

import java.nio.ByteBuffer;
import java.util.Arrays;

import static com.google.android.gms.vision.face.FaceDetector.ALL_LANDMARKS;

/**
 * This is a workaround for a bug in the face detector, in which either very small images (i.e.,
 * most images with dimension < 147) and very thin images can cause a crash in the native face
 * detection code.  This will add padding to such images before face detection in order to avoid
 * this issue.<p>
 * <p>
 * This is not necessary for use with the camera, which doesn't ever create these types of
 * images.<p>
 * <p>
 * This detector should wrap the underlying FaceDetector instance, like this:
 * <p>
 * Detector<Face> safeDetector = new SafeFaceDetector(faceDetector);
 * <p>
 * Replace all remaining occurrences of faceDetector with safeDetector.
 *
 * @author volobuev
 * @since 20.06.16
 */
public class SafeFaceDetector extends Detector<Face> implements IFaceDetector {
    private static final String TAG = "SafeFaceDetector";

    private Detector<Face> delegate;

    /**
     * Creates a safe face detector to wrap and protect an underlying face detector from images that
     * trigger the face detector bug.
     */
    public SafeFaceDetector(Context context) {
        this.delegate = new FaceDetector.Builder(context).setTrackingEnabled(false)
                                                         .setLandmarkType(ALL_LANDMARKS)
                                                         .build();
    }

    @Override
    public void release() {
        delegate.release();
    }

    /**
     * Determines whether the supplied image may cause a problem with the underlying face detector.
     * If it does, padding is added to the image in order to avoid the issue.
     */
    @Override
    public SparseArray<Face> detect(Frame frame) {
//        final int kMinDimension   = 147;
//        final int kDimensionLower = 640;
//        int       width           = frame.getMetadata().getWidth();
//        int       height          = frame.getMetadata().getHeight();
//
//        if (height > (2 * kDimensionLower)) {
//            // The image will be scaled down before detection is run.  Check to make sure that this
//            // won't result in the width going below the minimum
//            double multiple   = (double) height / (double) kDimensionLower;
//            double lowerWidth = Math.floor((double) width / multiple);
//            if (lowerWidth < kMinDimension) {
//                // The width would have gone below the minimum when downsampling, so apply padding
//                // to the right to keep the width large enough.
//                int newWidth = (int) Math.ceil(kMinDimension * multiple);
//                frame = padFrameRight(frame, newWidth);
//            }
//        } else if (width > (2 * kDimensionLower)) {
//            // The image will be scaled down before detection is run.  Check to make sure that this
//            // won't result in the height going below the minimum
//            double multiple    = (double) width / (double) kDimensionLower;
//            double lowerHeight = Math.floor((double) height / multiple);
//            if (lowerHeight < kMinDimension) {
//                int newHeight = (int) Math.ceil(kMinDimension * multiple);
//                frame = padFrameBottom(frame, newHeight);
//            }
//        } else if (width < kMinDimension) {
//            frame = padFrameRight(frame, kMinDimension);
//        }

        return delegate.detect(frame);
    }

    @Override
    public boolean isOperational() {
        return delegate.isOperational();
    }

    @Override
    public boolean setFocus(int id) {
        return delegate.setFocus(id);
    }

    /**
     * Creates a new frame based on the original frame, with additional width on the right to
     * increase the size to avoid the bug in the underlying face detector.
     */
    private Frame padFrameRight(Frame originalFrame, int newWidth) {
        Frame.Metadata metadata = originalFrame.getMetadata();
        int            width    = metadata.getWidth();
        int            height   = metadata.getHeight();

        Log.i(TAG, "Padded image from: " + width + "x" + height + " to " + newWidth + "x" + height);

        ByteBuffer origBuffer = originalFrame.getGrayscaleImageData();
        int        origOffset = origBuffer.arrayOffset();
        byte[]     origBytes  = origBuffer.array();

        // This can be changed to just .allocate in the future, when Frame supports non-direct
        // byte buffers.
        ByteBuffer paddedBuffer = ByteBuffer.allocateDirect(newWidth * height);
        int        paddedOffset = paddedBuffer.arrayOffset();
        byte[]     paddedBytes  = paddedBuffer.array();
        Arrays.fill(paddedBytes, (byte) 0);

        for (int y = 0; y < height; ++y) {
            int origStride   = origOffset + y * width;
            int paddedStride = paddedOffset + y * newWidth;
            System.arraycopy(origBytes, origStride, paddedBytes, paddedStride, width);
        }

        return new Frame.Builder().setImageData(paddedBuffer, newWidth, height, ImageFormat.NV21)
                                  .setId(metadata.getId())
                                  .setRotation(metadata.getRotation())
                                  .setTimestampMillis(metadata.getTimestampMillis())
                                  .build();
    }

    /**
     * Creates a new frame based on the original frame, with additional height on the bottom to
     * increase the size to avoid the bug in the underlying face detector.
     */
    private Frame padFrameBottom(Frame originalFrame, int newHeight) {
        Frame.Metadata metadata = originalFrame.getMetadata();
        int            width    = metadata.getWidth();
        int            height   = metadata.getHeight();

        Log.i(TAG, "Padded image from: " + width + "x" + height + " to " + width + "x" + newHeight);

        ByteBuffer origBuffer = originalFrame.getGrayscaleImageData();
        int        origOffset = origBuffer.arrayOffset();
        byte[]     origBytes  = origBuffer.array();

        // This can be changed to just .allocate in the future, when Frame supports non-direct
        // byte buffers.
        ByteBuffer paddedBuffer = ByteBuffer.allocateDirect(width * newHeight);
        int        paddedOffset = paddedBuffer.arrayOffset();
        byte[]     paddedBytes  = paddedBuffer.array();
        Arrays.fill(paddedBytes, (byte) 0);

        // Copy the image content from the original, without bothering to fill in the padded bottom
        // part.
        for (int y = 0; y < height; ++y) {
            int origStride   = origOffset + y * width;
            int paddedStride = paddedOffset + y * width;
            System.arraycopy(origBytes, origStride, paddedBytes, paddedStride, width);
        }

        return new Frame.Builder().setImageData(paddedBuffer, width, newHeight, ImageFormat.NV21)
                                  .setId(metadata.getId())
                                  .setRotation(metadata.getRotation())
                                  .setTimestampMillis(metadata.getTimestampMillis())
                                  .build();
    }

    @Override
    public boolean isFaceConform(byte[] faceData) {
        Bitmap bitmap = Util.convert(faceData);
        return isFaceConform(bitmap);
    }

    @Override
    public boolean isFaceConform(Bitmap bitmap) {
        long millisStart = System.currentTimeMillis();
        Frame             frame = new Frame.Builder().setBitmap(bitmap).build();
        Log.d(TAG, "Frame.Builder(): " + (System.currentTimeMillis() - millisStart) + " ms");
        millisStart = System.currentTimeMillis();
        SparseArray<Face> faces = detect(frame);
        Log.d(TAG, "detect(frame): " + (System.currentTimeMillis() - millisStart) + " ms");
        if (!isOperational()) {
            Log.w(TAG, "Face detector dependencies are not yet available.");
        } else {
            int size = faces.size();
            Log.d(TAG, "Face size = " + size);
            if (!(size == 1)) {
                return false;
            }
            Face face = null;
            millisStart = System.currentTimeMillis();
            for (int i = 0; i < Integer.MAX_VALUE && face == null; i++) {
                face = faces.get(i);
            }
            Log.d(TAG, "faces.get(i): " + (System.currentTimeMillis() - millisStart) + " ms");
            millisStart = System.currentTimeMillis();
            if (face != null) {
//                float leftEyeOpenProbability = face.getIsLeftEyeOpenProbability();
//                Log.d(TAG, "Left eye open probability = " + leftEyeOpenProbability);
//                float rightEyeOpenProbability = face.getIsRightEyeOpenProbability();
//                Log.d(TAG, "Right eye open probability = " + rightEyeOpenProbability);
//                boolean isLeftEyeOpen  = leftEyeOpenProbability >= 0.5;
//                boolean isRightEyeOpen = rightEyeOpenProbability >= 0.5;
//                Log.d(TAG, "Eyes opened = " + (isLeftEyeOpen && isRightEyeOpen));

                //            if (!(isLeftEyeOpen && isRightEyeOpen)) {
                //                return false;
                //            }

                Log.d(TAG, "Face width = " + face.getWidth());
                Log.d(TAG, "Bitmap width = " + bitmap.getWidth());
                boolean isRightProportion = 3 / 2 * face.getWidth() >= bitmap.getWidth();
                Log.d(TAG, "Face proportion is " + isRightProportion);
                Log.d(TAG, "Conditions: " + (System.currentTimeMillis() - millisStart) + " ms");
                return isRightProportion;
            }
        }
        return false;
    }
}
