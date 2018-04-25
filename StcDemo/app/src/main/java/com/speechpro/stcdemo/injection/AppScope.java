package com.speechpro.stcdemo.injection;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

/**
 * Created by Alexander Grigal on 23.01.18.
 */
@Documented
@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface AppScope {
}

