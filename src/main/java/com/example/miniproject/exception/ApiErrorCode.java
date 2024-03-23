package com.example.miniproject.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ApiErrorCode {

    DUPLICATED_EMAIL("Email is duplicated"),
    NOT_FOUND_MEMBER("Not found member"),
    NOT_MATCH_PASSWORD("Not match password"),
    INVALID_TOKEN("Token is invalid"),
    EXPIRED_TOKEN("Token is expired"),
    TOKEN_ERROR("Unknown token error"),

    NO_PERMISSION("You don't have permission"),

    NOT_FOUND_HOTEL("Not found hotel"),

    NOT_FOUND_ROOM("Not found room"),

    NOT_FOUND_IMAGE("Not found image"),

    NOT_FOUND_NOTICE("Not found notice"),

    NOT_FOUND_ORDER("Not found order"),

    FIREBASE_EXCEPTION("Firebase Error"),
    INTERNAL_SERVER_ERROR("server error"),

    NOT_AVAILABLE_ROOM("Room is not available"),
    EXCEEDS_MAXIMUM_CAPACITY("Exceeds maximum capacity");

    private final String description;

}
