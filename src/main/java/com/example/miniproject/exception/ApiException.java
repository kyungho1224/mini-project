package com.example.miniproject.exception;

import lombok.Getter;

@Getter
public class ApiException extends RuntimeException {

    private final String errorDescription;

    public ApiException(String errorDescription) {
        super(errorDescription);
        this.errorDescription = errorDescription;
    }

}
