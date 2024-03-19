package com.example.miniproject.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ApiErrorCode implements ErrorCode {

    DUPLICATED_EMAIL(HttpStatus.CONFLICT.value(), "Email is duplicated"),
    NOT_FOUND_MEMBER(HttpStatus.NOT_FOUND.value(), "Not found member"),
    NOT_MATCH_PASSWORD(HttpStatus.UNAUTHORIZED.value(), "Not match password"),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED.value(), "Token is invalid"),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED.value(), "Token is expired"),
    TOKEN_ERROR(HttpStatus.UNAUTHORIZED.value(), "Unknown token error"),
    FIREBASE_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Firebase Error"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), "server error"),
    ;

    private final Integer httpStatusCode;
    private final String description;

}
