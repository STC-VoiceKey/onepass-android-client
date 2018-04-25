/*
 * Copyright (C) The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.speechpro.onepass.framework.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import com.google.android.gms.vision.face.Face;
import com.speechpro.onepass.framework.R;
import com.speechpro.onepass.framework.util.Utils;

/**
 * Graphic instance for rendering face position, orientation, and landmarks within an associated
 * graphic overlay view.
 */
public class FaceGraphic extends GraphicOverlayView.Graphic {

    private static final String TAG = FaceGraphic.class.getSimpleName();

    private static final float FACE_POSITION_RADIUS = 10.0f;
    private static final float ID_TEXT_SIZE = 40.0f;
    private static final float ID_Y_OFFSET = 50.0f;
    private static final float ID_X_OFFSET = -50.0f;
    private static final float BOX_STROKE_WIDTH = 5.0f;

    private static final int COLOR_CHOICES[] = {
            Color.BLUE,
            Color.CYAN,
            Color.GREEN,
            Color.MAGENTA,
            Color.WHITE,
            Color.YELLOW
    };
    private static int mCurrentColorIndex = 0;

    private Paint mOuterBorderPaint;
    private Paint mBorderPaint;
    private Paint mFacePositionPaint;
    private Paint mIdPaint;
    private Paint mBoxPaint;

    private final Context mApplicationContext;
    private final GraphicOverlayView mOverlay;
    private final FaceCallback mCallback;

    private final int mToobarInPx;
    private final int mBorderInPx;

    private volatile Face mFace;
    private int mFaceId;

    private boolean mHasBorder;

    public FaceGraphic(GraphicOverlayView overlay, FaceCallback callback, boolean hasBorder) {
        super(overlay);
        mOverlay = overlay;
        mCallback = callback;
        mHasBorder = hasBorder;
        mApplicationContext = overlay.getContext().getApplicationContext();

        mCurrentColorIndex = (mCurrentColorIndex + 1) % COLOR_CHOICES.length;
        final int selectedColor = COLOR_CHOICES[mCurrentColorIndex];

        mFacePositionPaint = new Paint();
        mFacePositionPaint.setColor(selectedColor);

        mIdPaint = new Paint();
        mIdPaint.setColor(selectedColor);
        mIdPaint.setTextSize(ID_TEXT_SIZE);

        mBoxPaint = new Paint();
        mBoxPaint.setColor(selectedColor);
        mBoxPaint.setStyle(Paint.Style.STROKE);
        mBoxPaint.setStrokeWidth(BOX_STROKE_WIDTH);

        mOuterBorderPaint = new Paint();
        mOuterBorderPaint.setColor(Color.RED);
        mOuterBorderPaint.setStyle(Paint.Style.STROKE);
        mOuterBorderPaint.setStrokeWidth(BOX_STROKE_WIDTH);

        mBorderPaint = new Paint();
        mBorderPaint.setColor(Color.RED);
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setStrokeWidth(BOX_STROKE_WIDTH);

        mToobarInPx = mApplicationContext.getResources().getDimensionPixelSize(R.dimen.app_toolbar_height);
        mBorderInPx = Utils.dpToPx(56);
    }

    public void setId(int id) {
        mFaceId = id;
    }


    /**
     * Updates the face instance from the detection of the most recent frame.  Invalidates the
     * relevant portions of the overlay to trigger a redraw.
     */
    public void updateFace(Face face) {
        mFace = face;
        postInvalidate();
    }

    public void goneFace() {
        mFace = null;
    }

    /**
     * Draws the face annotations for position on the supplied canvas.
     */
    @Override
    public void draw(Canvas canvas) {
        Face face = mFace;
        if (face == null) {
            return;
        }

        Log.d(TAG, "root view: " + mOverlay.getRootView().getWidth() + "x" + mOverlay.getRootView().getHeight());
        Log.d(TAG, "canvas: " + canvas.getWidth() + "x" + canvas.getHeight());

        // Draws red border
        int xDelta = (canvas.getWidth() - mOverlay.getRootView().getWidth()) / 2;
        int yDelta = (canvas.getHeight() - mOverlay.getRootView().getHeight()) / 2;
        float leftBorder = mBorderInPx + xDelta;
        float topBorder = mToobarInPx * 1.5f;
        float rightBorder = mOverlay.getRootView().getWidth() - mBorderInPx + xDelta;
        float bottomBorder = mOverlay.getRootView().getHeight() - (mBorderInPx * 3.2f);
        if (mHasBorder)
            canvas.drawRect(leftBorder, topBorder, rightBorder, bottomBorder, mBorderPaint);

        // Draws outer red border
        float leftOuterBorder = xDelta;
        float topOuterBorder = 0;
        float rightOuterBorder = mOverlay.getRootView().getWidth() + xDelta;
        float bottomOuterBorder = mOverlay.getRootView().getHeight();
        if (mHasBorder)
            canvas.drawRect(leftOuterBorder, topOuterBorder, rightOuterBorder, bottomOuterBorder, mOuterBorderPaint);

        float x = translateX(face.getPosition().x + face.getWidth() / 2);
        float y = translateY(face.getPosition().y + face.getHeight() / 2);

        if (mHasBorder) {
            canvas.drawCircle(x, y, FACE_POSITION_RADIUS, mFacePositionPaint);
            canvas.drawText("id: " + mFaceId, x + ID_X_OFFSET, y + ID_Y_OFFSET, mIdPaint);
            canvas.drawText("happiness: " + String.format("%.2f", face.getIsSmilingProbability()),
                    x - ID_X_OFFSET, y - ID_Y_OFFSET, mIdPaint);
            canvas.drawText("right eye: " + String.format("%.2f", face.getIsRightEyeOpenProbability()),
                    x + ID_X_OFFSET * 2, y + ID_Y_OFFSET * 2, mIdPaint);
            canvas.drawText("left eye: " + String.format("%.2f", face.getIsLeftEyeOpenProbability()),
                    x - ID_X_OFFSET * 2, y - ID_Y_OFFSET * 2, mIdPaint);
        }

        // Draws a bounding box around the face.
        float xOffset = scaleX(face.getWidth() / 2.0f);
        float yOffset = scaleY(face.getHeight() / 2.0f);
        float left = x - xOffset;
        float top = y - yOffset;
        float right = x + xOffset;
        float bottom = y + (yOffset * 1.2f);
        if (mHasBorder)
            canvas.drawRect(left, top, right, bottom, mBoxPaint);

        boolean inArea = inArea(
                leftBorder, topBorder, rightBorder, bottomBorder,
                left, top, right, bottom,
                leftOuterBorder, topOuterBorder, rightOuterBorder, bottomOuterBorder);

        if (mCallback != null) {
            mCallback.onInArea(inArea);
        }

    }

    private boolean inArea(float left1, float top1, float right1, float bottom1,
                           float left2, float top2, float right2, float bottom2,
                           float left3, float top3, float right3, float bottom3) {

        return ((left1 > left2 && top1 > top2 && right1 < right2 && bottom1 < bottom2)
                && (left3 < left2 && top3 < top2 && right3 > right2 && bottom3 > bottom2));
    }
}
