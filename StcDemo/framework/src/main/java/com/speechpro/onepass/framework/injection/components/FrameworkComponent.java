package com.speechpro.onepass.framework.injection.components;

import com.speechpro.onepass.core.transport.ITransport;
import com.speechpro.onepass.framework.injection.modules.FrameworkModule;
import dagger.Component;

import javax.inject.Singleton;

/**
 * @author volobuev
 * @since 31.05.16
 */
@Singleton
@Component(modules = FrameworkModule.class)
public interface FrameworkComponent {

    ITransport transport();
}
