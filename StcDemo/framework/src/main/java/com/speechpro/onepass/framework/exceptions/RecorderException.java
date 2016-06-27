package com.speechpro.onepass.framework.exceptions;

import java.io.IOException;

/**
 * @author volobuev
 * @since 30.03.16
 */
public class RecorderException extends IOException {
    public RecorderException(String detailMessage) {
        super(detailMessage);
    }
}
