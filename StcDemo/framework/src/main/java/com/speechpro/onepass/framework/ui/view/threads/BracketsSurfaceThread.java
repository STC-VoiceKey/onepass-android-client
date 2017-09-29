package com.speechpro.onepass.framework.ui.view.threads;

import android.content.Context;
import android.graphics.*;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Surface;
import com.speechpro.onepass.framework.R;
import com.speechpro.onepass.framework.ui.view.graphics.Circle;
import com.speechpro.onepass.framework.ui.view.graphics.CirclesPair;
import com.speechpro.onepass.framework.util.DimensionUtils;

import java.util.*;


/**
 * @author volobuev
 * @since 11.10.16
 */
public class BracketsSurfaceThread extends UiThread {

    private final static String TAG = BracketsSurfaceThread.class.getName();

    //States
    public final static int START_STATE   = 0;
    public final static int FACE_DETECTED = 1;
    public final static int FACE_LOST     = 2;

    // Circle rows count
    public final static int ROWS = 20;

    //Message data tags
    public final static String COUNT = "COUNT";

    private float mHeight;
    private float mWidth;
    private int   mCount;

    private final int mBackgroundColor;
    private final int mFaceFrameColor;
    private final int mDefaultColor;
    private final int mDetectedColor;
    private final int mLostColor;
    private final int mCursorLength;
    private final int mCursorDelta;

    private final Paint mTransparentPaint;
    private final Paint mFaceFramePaint;
    private final Paint mStartPaint;
    private final Paint mDetectPaint;
    private final Paint mLostPaint;

    private List<CirclesPair> mCirclesPairs;


    public BracketsSurfaceThread(SurfaceTexture mTexture, Context mContext, float mHeight, float mWidth) {

        super(mTexture, mContext);
        this.mHeight = mHeight;
        this.mWidth = mWidth;
        this.mCount = 0;

        mBackgroundColor = ContextCompat.getColor(mContext, R.color.colorBorderBack);
        mFaceFrameColor = ContextCompat.getColor(mContext, android.R.color.transparent);
        mDetectedColor = ContextCompat.getColor(mContext, R.color.colorValid);
        mDefaultColor = ContextCompat.getColor(mContext, R.color.colorWaiting);
        mLostColor = ContextCompat.getColor(mContext, R.color.colorInvalid);

        mCursorDelta = DimensionUtils.convertDipToPixels(mContext, 2) / 2;
        mCursorLength = DimensionUtils.convertDipToPixels(mContext, 36);

        mFaceFramePaint = new Paint();
        mFaceFramePaint.setColor(mFaceFrameColor);
        mFaceFramePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

        mTransparentPaint = new Paint();
        mTransparentPaint.setColor(mFaceFrameColor);
        mTransparentPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

        mStartPaint = new Paint();
        mStartPaint.setAntiAlias(true);
        mStartPaint.setColor(mDefaultColor);
        mStartPaint.setStyle(Paint.Style.STROKE);
        mStartPaint.setStrokeWidth(mCursorDelta * 2);

        mDetectPaint = new Paint();
        mDetectPaint.setAntiAlias(true);
        mDetectPaint.setColor(mDetectedColor);
        mDetectPaint.setStyle(Paint.Style.STROKE);
        mDetectPaint.setStrokeWidth(mCursorDelta * 2);

        mLostPaint = new Paint();
        mLostPaint.setAntiAlias(true);
        mLostPaint.setColor(mLostColor);
        mLostPaint.setStyle(Paint.Style.STROKE);
        mLostPaint.setStrokeWidth(mCursorDelta * 2);

        mCirclesPairs = prepareCircles();

    }

    @Override
    public void run() {
        super.run();
        drawBrackets(START_STATE, 0);
        mHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                Bundle stateData = msg.getData();
                if (stateData != null) {
                    mCount = stateData.getInt(COUNT);
                }
                switch (msg.what) {
                    case START_STATE:
                        drawBrackets(START_STATE, 0);
                        break;
                    case FACE_DETECTED:
                        drawBrackets(FACE_DETECTED, mCount);
                        break;
                    case FACE_LOST:
                        drawBrackets(FACE_LOST, mCount);
                        break;
                }
                return true;
            }
        });
        Looper.loop();
    }

    private void drawBrackets(int type, int count) {
        Canvas  canvas  = null;
        Surface surface = null;
        try {
            synchronized (mTexture) {
                surface = new Surface(mTexture);
            }
            canvas = surface.lockCanvas(null);
            canvas.drawColor(mFaceFrameColor, PorterDuff.Mode.CLEAR);
            canvas.drawColor(mFaceFrameColor);

            Paint requiredPaint;
            switch (type) {
                case FACE_DETECTED:
                    requiredPaint = mDetectPaint;
                    break;
                case FACE_LOST:
                    requiredPaint = mLostPaint;
                    break;
                default:
                    requiredPaint = mStartPaint;
                    break;
            }

            int i = 0;
            for (; i < count; i++) {
                mCirclesPairs.get(i).draw(canvas, requiredPaint);
            }
            for (; i < ROWS; i++) {
                mCirclesPairs.get(i).draw(canvas, mStartPaint);
            }

        } finally {
            if (canvas != null) { surface.unlockCanvasAndPost(canvas); }
            if (surface != null) { surface.release(); }
        }

    }

    // TODO: Rewrite! It needs to have equal path between dots.
    private List<CirclesPair> prepareCircles() {

        // Distance between circles on X axis
        float distX = 0;
        // Distance between circles on Y axis
        int DIST_Y = 50;
        // Circle radius
        float RADIUS = 15;

        // Margin from top
        float topMargin = 0;

        // Origin of coordinate
        float xCenter = mWidth / 2;
        float yCenter = mHeight / 2 + topMargin;

        // Range of curvature
        float curvature = 60;

        // Focal length
        float focal = mWidth / (float) 2.5;

        List<CirclesPair> circles = new ArrayList<>(ROWS);

        float x0Left = calcX(0, 0, focal, false);
        float y0Left = calcY(curvature, x0Left, focal, false);
        Log.d(TAG, "Point 0 left (" + x0Left + ", " + y0Left + ")");

        float x0Right = calcX(0, 0, focal, true);
        float y0Right = calcY(curvature, x0Right, focal, true);
        Log.d(TAG, "Point 0 right (" + x0Right + ", " + y0Right + ")");


        for (int i = 0; i < ROWS / 2; i++) {

            // These coordinates correspond to the coordinates of the parabola rotated
            float x1Left;
            float y1Left;
            float y2Left;
            float dist;
            float thresholdLow    = 75;
            float thresholdHeight = 85;

            if (i == 0) {
                thresholdLow = 45;
                thresholdHeight = 55;
            }

            do {
                x1Left = calcX(distX, i, focal, false);
                y1Left = calcY(curvature, x1Left, focal, false);
                y2Left = -y1Left;
                dist = calcDist(x0Left, y0Left, x1Left, y1Left);
                distX += 0.001;
            } while (dist < thresholdLow);
            Log.d(TAG, "Dist left " + dist);
            Log.d(TAG, "Point " + i + " left (" + x1Left + ", " + y1Left + ")");
            Log.d(TAG, "Point " + i + " left (" + x1Left + ", " + y2Left + ")");

            Circle leftTop    = new Circle(x1Left + xCenter, y1Left + yCenter, RADIUS);
            Circle leftBottom = new Circle(x1Left + xCenter, y2Left + yCenter, RADIUS);
            x0Left = x1Left;
            y0Left = y1Left;

            float x1Right = -x1Left;
            float y1Right = y1Left;
            float y2Right = y2Left;
            distX = 0;

            Log.d(TAG, "Dist right " + dist);
            Log.d(TAG, "Point " + i + " right (" + x1Right + ", " + y1Right + ")");
            Log.d(TAG, "Point " + i + " right (" + x1Right + ", " + y2Right + ")");

            Circle rightTop    = new Circle(x1Right + xCenter, y1Right + yCenter, RADIUS);
            Circle rightBottom = new Circle(x1Right + xCenter, y2Right + yCenter, RADIUS);
            x0Right = x1Right;
            y0Right = y1Right;

            CirclesPair top    = new CirclesPair(leftTop, rightTop);
            CirclesPair bottom = new CirclesPair(leftBottom, rightBottom);
            circles.add(top);
            circles.add(bottom);

        }

        Collections.sort(circles, new Comparator<CirclesPair>() {
            @Override
            public int compare(CirclesPair lhs, CirclesPair rhs) {
                return (int) (rhs.first.getY() - lhs.first.getY());
            }
        });
        return circles;
    }

    private float calcX(float distX, int count, float focal, boolean sign) {
        int signI = sign ? -1 : 1;
        return signI * (distX + 7*count) - signI * focal;
    }

    private float calcY(float curvature, float x, float focal, boolean sign) {
        int signI = sign ? -1 : 1;
        return curvature * (float) Math.sqrt(Math.abs(x + signI * focal));
    }

    private float calcDist(float x1, float y1, float x2, float y2) {
        return (float) Math.sqrt(Math.pow(y2 - y1, 2) + Math.pow(x2 - x1, 2));
    }

    /**
     * This method adds circles for debug.
     * Circles will placed in corners.
     *
     * @param circles list which contain circles
     */
    private void addDebugCircles(List<CirclesPair> circles) {

        // Circle radius
        float RADIUS = 15;

        CirclesPair top = new CirclesPair(new Circle(RADIUS, RADIUS, RADIUS),
                                          new Circle(mWidth - RADIUS, RADIUS, RADIUS));
        CirclesPair bottom = new CirclesPair(new Circle(RADIUS, mHeight - RADIUS, RADIUS),
                                             new Circle(mWidth - RADIUS, mHeight - RADIUS, RADIUS));

        circles.add(top);
        circles.add(bottom);
    }

}
