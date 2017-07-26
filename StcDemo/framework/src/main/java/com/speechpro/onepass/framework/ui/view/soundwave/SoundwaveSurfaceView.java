package com.speechpro.onepass.framework.ui.view.soundwave;

import android.content.Context;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

/**
 * Created by grigal on 15.05.2017.
 */

public class SoundwaveSurfaceView extends GLSurfaceView {

    private static final String TAG = SoundwaveSurfaceView.class.getSimpleName();

    private SoundwaveRender mRenderer;


    public SoundwaveSurfaceView(Context context) {
        super(context);
        init(context);
    }

    public SoundwaveSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        setEGLContextClientVersion(2);
        getHolder().setFormat(PixelFormat.TRANSLUCENT);
        setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        mRenderer = new SoundwaveRender(context);
        setRenderer(mRenderer);
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        setZOrderOnTop(true);
    }

    public void addVerticalLine(float amplitude) {
        mRenderer.addLine(amplitude == 0 ? 0.01f : amplitude);
    }

    public void clear() {
        mRenderer.resetVertexData();
    }
}
