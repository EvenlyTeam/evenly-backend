package com.evenly.common.domain;

/** 인증은 되었으나 권한이 없는 경우. HTTP 403 으로 매핑된다. */
public class ForbiddenException extends RuntimeException {

    public ForbiddenException(String message) {
        super(message);
    }
}
