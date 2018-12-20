package com.speechpro.onepass.framework.injection.components;

import android.content.Context;

import com.speechpro.onepass.framework.injection.modules.UIModule;
import com.speechpro.onepass.framework.model.IModel;
import com.speechpro.onepass.framework.ui.activity.BaseActivity;
import com.speechpro.onepass.framework.ui.activity.EnrollmentActivity;
import com.speechpro.onepass.framework.ui.activity.VerificationActivity;
import com.speechpro.onepass.framework.ui.fragment.enroll.EnrollDynamicEnrollVoiceFragment;

import javax.inject.Singleton;

import dagger.Component;

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

    void inject(EnrollDynamicEnrollVoiceFragment fragment);

    Context context();
    IModel model();
}
