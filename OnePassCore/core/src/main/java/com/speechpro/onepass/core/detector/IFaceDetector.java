package com.speechpro.onepass.core.detector;

import android.graphics.Bitmap;

/**
 * Detects face and checks face conformity for enrollment and verification.
 * Face must have proportion 2/3 to image size. Image must have one face.
 *
 * @author volobuev
 * @since 25.01.2016
 */
public interface IFaceDetector {

    /**
     * Shows what image have a face and this face is only one.
     * The face is conform rules for enrollment and verification.
     *
     * @param faceData face data
     * @return true - if face is conform, false - otherwise
     */
    boolean isFaceConform(byte[] faceData);


    /**
     * Shows what image have a face and this face is only one.
     * The face is conform rules for enrollment and verification.
     *
     * @param bitmap face data
     * @return true - if face is conform, false - otherwise
     */
    boolean isFaceConform(Bitmap bitmap);
}
