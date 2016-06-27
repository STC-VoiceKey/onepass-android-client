package com.speechpro.onepass.framework.injection.components;

import com.speechpro.onepass.framework.injection.PerActivity;
import com.speechpro.onepass.framework.injection.modules.ActivityModule;
import com.speechpro.onepass.framework.view.fragment.FaceFragment;
import com.speechpro.onepass.framework.view.fragment.FaceBorderFragment;
import com.speechpro.onepass.framework.view.fragment.VoiceFragment;
import dagger.Component;

/**
 * @author volobuev
 * @since 11.04.16
 */
@PerActivity
@Component(dependencies = UIComponent.class, modules = {ActivityModule.class})
public interface FragmentComponent extends ActivityComponent {

    void inject(VoiceFragment voiceFragment);

    void inject(FaceBorderFragment faceBorderFragment);

    void inject(FaceFragment faceFragment);

}
