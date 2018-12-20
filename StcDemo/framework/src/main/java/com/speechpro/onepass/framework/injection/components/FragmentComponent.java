package com.speechpro.onepass.framework.injection.components;

import com.speechpro.onepass.framework.injection.PerActivity;
import com.speechpro.onepass.framework.injection.modules.ActivityModule;
import com.speechpro.onepass.framework.ui.fragment.enroll.EnrollDynamicEnrollVoiceFragment;

import dagger.Component;

/**
 * @author volobuev
 * @since 11.04.16
 */
@PerActivity
@Component(dependencies = UIComponent.class, modules = {ActivityModule.class})
public interface FragmentComponent extends ActivityComponent {

    void inject(EnrollDynamicEnrollVoiceFragment enrollDynamicVoiceFragment);

}
