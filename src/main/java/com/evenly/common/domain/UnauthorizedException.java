package com.evenly.common.domain;

/** 인증 실패(자격 증명 오류 등). HTTP 401 로 매핑된다. */
public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String message) {
        super(message);
    }
}
