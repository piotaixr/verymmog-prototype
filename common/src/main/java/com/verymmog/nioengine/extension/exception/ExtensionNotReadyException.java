package com.verymmog.nioengine.extension.exception;

public class ExtensionNotReadyException extends RuntimeException {

    public ExtensionNotReadyException(String message) {
        super(message);
    }

    public static ExtensionNotReadyException engineIsNull() {
        return new ExtensionNotReadyException("Extension not ready: engine is null");
    }
}
