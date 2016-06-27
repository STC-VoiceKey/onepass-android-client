package com.speechpro.onepass.framework.injection;


import com.speechpro.onepass.core.transport.ITransport;
import com.speechpro.onepass.framework.injection.components.DaggerFrameworkComponent;
import com.speechpro.onepass.framework.injection.components.FrameworkComponent;
import com.speechpro.onepass.framework.injection.modules.FrameworkModule;

/**
 * @author volobuev
 * @since 31.05.16
 */
public class FrameworkInjection {

    private final static FrameworkComponent frameworkComponent = DaggerFrameworkComponent.builder()
                                                                    .frameworkModule(new FrameworkModule())
                                                                    .build();

    public static ITransport getTransport(){
        return frameworkComponent.transport();
    }


}
