package com.speechpro.onepass.framework.view.fragment;

import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.*;
import com.speechpro.onepass.framework.R;
import com.speechpro.onepass.framework.camera.PreviewCallback;
import com.speechpro.onepass.framework.injection.components.UIComponent;
import com.speechpro.onepass.framework.presenter.EnrollmentPresenter;
import com.speechpro.onepass.framework.view.VisionView;
import com.speechpro.onepass.framework.view.activity.BaseActivity;
import com.speechpro.onepass.framework.view.callbacks.SurfaceCallback;

import javax.inject.Inject;

/**
 * @author volobuev
 * @since 14.06.16
 */
public abstract class VisionFragment extends BaseFragment implements VisionView {

    @Inject
    PreviewCallback previewCallback;

    protected BaseActivity activity;
    protected FragmentShower shower;
    protected SurfaceView preview;
    protected Camera camera;

    private SurfaceCallback callback;
    private SurfaceHolder surfaceHolder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.f_camera, container, false);
        preview = (SurfaceView) view.findViewById(R.id.preview);
        activity = (BaseActivity) getActivity();
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        shower = new FragmentShower(activity, this);
        this.initialize();
    }

    @Override
    public void onResume() {
        super.onResume();
        init();
    }

    @Override
    public void onPause() {
        super.onPause();
        previewCallback.onDetach();
        if (camera != null) {
            camera.setPreviewCallback(null);
            camera.stopPreview();
            camera.release();
            camera = null;
            surfaceHolder.removeCallback(callback);
        }
    }

    @Override
    public Camera getCamera() {
        return camera;
    }

    @Override
    protected boolean isEnrollment() {
        return activity.getPresenter() instanceof EnrollmentPresenter;
    }

    private void initialize() {
        shower.getComponent(UIComponent.class).inject(this);
    }

    private void init() {
        initCamera();

        previewCallback.setParameters(camera.getParameters());
        callback = new SurfaceCallback(camera, previewCallback, preview);
        surfaceHolder = preview.getHolder();
        surfaceHolder.setFormat(PixelFormat.TRANSLUCENT);
        surfaceHolder.addCallback(callback);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        previewCallback.onAttach();

    }

    private void initCamera() {
        int cameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
        camera = Camera.open(cameraId);

        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);

        camera.setDisplayOrientation((360 - info.orientation) % 360);
    }
}
