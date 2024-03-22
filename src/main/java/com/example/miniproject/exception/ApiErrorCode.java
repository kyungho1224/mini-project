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

    NO_PERMISSION(HttpStatus.FORBIDDEN.value(), "You don't have permission"),

    NOT_FOUND_HOTEL(HttpStatus.NOT_FOUND.value(), "Not found hotel"),

    NOT_FOUND_ROOM(HttpStatus.NOT_FOUND.value(), "Not found room"),

    NOT_FOUND_IMAGE(HttpStatus.NOT_FOUND.value(), "Not found image"),

    NOT_FOUND_NOTICE(HttpStatus.NOT_FOUND.value(), "Not found notice"),

    NOT_FOUND_ORDER(HttpStatus.NOT_FOUND.value(), "Not found order"),

    FIREBASE_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Firebase Error"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), "server error"),

    NOT_AVAILABLE_ROOM(HttpStatus.BAD_REQUEST.value(), "Room is not available"),
    EXCEEDS_MAXIMUM_CAPACITY(HttpStatus.BAD_REQUEST.value(), "Exceeds maximum capacity")
    ;

    private final Integer httpStatusCode;
    private final String description;

}
