package com.example.miniproject.exception;

import com.example.miniproject.common.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ApiResponse<String> apiExceptionHandler(ApiException ex) {
        return new ApiResponse<>(ex.getErrorCode().getHttpStatusCode(), ex.getErrorDescription());
    }

    @ExceptionHandler(RuntimeException.class)
    public ApiResponse<String> allExceptionHandler(RuntimeException ex) {
        return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<String> validExceptionHandler(MethodArgumentNotValidException ex) {
        return ApiResponse.error(HttpStatus.BAD_REQUEST, ex.getBindingResult().getAllErrors().get(0).getDefaultMessage());
    }

}
