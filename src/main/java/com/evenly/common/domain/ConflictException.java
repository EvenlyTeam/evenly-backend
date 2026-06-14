package com.evenly.common.domain;

/** 도메인 제약 충돌(중복 등). HTTP 409 로 매핑된다. */
public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }
}
