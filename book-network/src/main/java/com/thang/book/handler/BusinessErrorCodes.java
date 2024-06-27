package com.thang.book.handler;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public enum BusinessErrorCodes {
        NO_CODE(0, HttpStatus.NOT_IMPLEMENTED, "No code"),

        INCORRECT_CURRENT_PASSWORD(300, HttpStatus.BAD_REQUEST, "Current Pass is wrong"),

        NEW_PASSWORD_DOES_NOT_MATCH(301, HttpStatus.BAD_REQUEST, "The new pass is not match"),

        ACCOUNT_LOCKED(302, HttpStatus.FORBIDDEN, "account is locked"),

        ACCOUNT_DISABLED(303, HttpStatus.FORBIDDEN, "account is disabled"),

        BAD_CREDENTIALS(304, HttpStatus.FORBIDDEN, "Pass or email is not correct"),
    ;

    @Getter
    private int code;
    @Getter
    private String des;
    @Getter
    private HttpStatus status;

    BusinessErrorCodes(int code, HttpStatus status, String des) {
        this.code = code;
        this.des = des;
        this.status = status;
    }
}
