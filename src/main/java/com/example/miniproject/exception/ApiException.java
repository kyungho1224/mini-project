package com.example.miniproject.exception;

import lombok.Getter;

@Getter
public class ApiException extends RuntimeException {

    private final ErrorCode errorCode;
    private final String errorDescription;

    public ApiException(ErrorCode errorCode) {
        super(errorCode.getDescription());
        this.errorCode = errorCode;
        this.errorDescription = errorCode.getDescription();
    }

    public ApiException(ErrorCode errorCode, String errorDescription) {
        super(errorCode.getDescription());
        this.errorCode = errorCode;
        this.errorDescription = errorDescription;
    }

}
