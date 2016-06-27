package com.speechpro.onepass.core.exception;

/**
 * CoreException is occurring in Core SDK.
 *
 * @author volobuev
 * @since 24.09.2015.
 */
public class CoreException extends Exception {

	protected CoreException() {
	}

	protected CoreException(final String detailMessage) {
		super(detailMessage);
	}

	protected CoreException(final String detailMessage, final Throwable throwable) {
		super(detailMessage, throwable);
	}

	protected CoreException(final Throwable throwable) {
		super(throwable);
	}
}
