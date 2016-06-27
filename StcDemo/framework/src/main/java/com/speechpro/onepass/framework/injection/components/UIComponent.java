package com.speechpro.onepass.framework.injection.components;

import android.content.Context;
import com.speechpro.onepass.core.detector.IFaceDetector;
import com.speechpro.onepass.framework.camera.PreviewCallback;
import com.speechpro.onepass.framework.injection.modules.UIModule;
import com.speechpro.onepass.framework.media.AudioRecorder;
import com.speechpro.onepass.framework.model.IModel;
import com.speechpro.onepass.framework.view.activity.BaseActivity;
import com.speechpro.onepass.framework.view.activity.EnrollmentActivity;
import com.speechpro.onepass.framework.view.activity.VerificationActivity;
import com.speechpro.onepass.framework.view.fragment.VideoBorderFragment;
import com.speechpro.onepass.framework.view.fragment.VisionFragment;
import com.speechpro.onepass.framework.view.fragment.FaceBorderFragment;
import com.speechpro.onepass.framework.view.fragment.VoiceFragment;
import dagger.Component;

import javax.inject.Singleton;

/**
 * @author volobuev
 * @since 31.03.16
 */
@Singleton
@Component(modules = UIModule.class)
public interface UIComponent {

    void inject(BaseActivity baseActivity);
    void inject(EnrollmentActivity enrollmentActivity);
    void inject(VerificationActivity verificationActivity);

    void inject(FaceBorderFragment faceBorderFragment);
    void inject(VideoBorderFragment faceBorderFragment);
    void inject(VoiceFragment fragment);
    void inject(VisionFragment faceFragment);

    void inject(PreviewCallback previewCallback);

    Context context();
    IModel model();
    AudioRecorder audioRecorder();
    PreviewCallback previewCallback();
//    IFaceDetector faceDetector();
}
