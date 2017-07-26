package com.speechpro.onepass.framework.ui.view.soundwave;

import android.app.Activity;
import android.content.Context;
import android.opengl.GLSurfaceView;

import com.speechpro.onepass.framework.R;
import com.speechpro.onepass.framework.util.DisplayUtils;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_FRAGMENT_SHADER;
import static android.opengl.GLES20.GL_LINES;
import static android.opengl.GLES20.GL_VERTEX_SHADER;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glLineWidth;
import static android.opengl.GLES20.glUniform4f;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES20.glViewport;

/**
 * Created by grigal on 15.05.2017.
 */

public class SoundwaveRender implements GLSurfaceView.Renderer {

    private static final String TAG = SoundwaveRender.class.getSimpleName();

    private Context mContext;

    private int mProgramId;

    private FloatBuffer mVertexData;
    private float[] mVertices;
    private int cFig;

    private int mColorLocation;
    private int mPositionLocation;

    private final float mScalingRatio;
    private final float mLineWidth;
    private float mOffset;

    private final float aFloat = 42000f;

    public SoundwaveRender(Context context) {
        this.mContext = context;
        this.mScalingRatio = DisplayUtils.getScalingRatio((Activity) context);
        this.mLineWidth = this.mScalingRatio;
//        this.mLineWidth = this.mScalingRatio < 3 ? 1 : 4;

        initByteBuffer();
    }

    private void initVertices() {
        mVertices = new float[]{-1.0f, 0.0f, -1.0f, 0.0f,};
    }

    public void resetVertexData() {
        initVertices();
        mVertexData.clear();
        mVertexData.put(mVertices);
        mVertexData.position(0);
        cFig = 0;
    }

    private void initByteBuffer() {
        initVertices();
        mVertexData = ByteBuffer
                .allocateDirect(4096)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        mVertexData.put(mVertices);
    }

    private void bindData() {
        mColorLocation = glGetUniformLocation(mProgramId, "u_Color");
        glUniform4f(mColorLocation, 1.0f, 1.0f, 0.0f, 1.0f);

        mPositionLocation = glGetAttribLocation(mProgramId, "a_Position");
        mVertexData.position(0);
        glVertexAttribPointer(mPositionLocation, 2, GL_FLOAT,
                false, 0, mVertexData);
        glEnableVertexAttribArray(mPositionLocation);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        int vertexShaderId = ShaderUtils.createShader(
                mContext, GL_VERTEX_SHADER, R.raw.vertex_shader);
        int fragmentShaderId = ShaderUtils.createShader(
                mContext, GL_FRAGMENT_SHADER, R.raw.fragment_shader);
        mProgramId = ShaderUtils.createProgram(vertexShaderId, fragmentShaderId);
        glUseProgram(mProgramId);
        bindData();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        glViewport(0, 0, width, height);
        mOffset = ((float) width) / (mScalingRatio * aFloat);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        glClear(GL_COLOR_BUFFER_BIT);
        glLineWidth(mLineWidth);
        glDrawArrays(GL_LINES, 0, cFig);
    }

    public void addLine(float amplitude) {
        mVertices = addElement(mVertices, Math.abs(amplitude), mOffset);
        try {
            mVertexData.put(mVertices);
        } catch (BufferOverflowException e) {
            e.printStackTrace();
        }
    }

    private float[] addElement(float[] a, float e, float offset) {
        cFig += 2;
        float x = a[a.length - 2] + offset;
        return new float[]{x, -e, x, e,};
    }

}
