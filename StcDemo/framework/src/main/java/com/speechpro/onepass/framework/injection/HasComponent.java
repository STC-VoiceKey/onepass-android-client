package com.speechpro.onepass.framework.injection;

/**
 * Interface representing a contract for clients that contains a component for dependency injection.
 *
 * @author volobuev
 * @since 14.01.2016
 */
public interface HasComponent<C> {
    C getComponent();
}
