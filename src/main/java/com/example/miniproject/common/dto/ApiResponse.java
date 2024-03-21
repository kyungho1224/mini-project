package com.example.miniproject.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ApiResponse<T> {

    private Integer resultCode;
    private T data;

    public ApiResponse(Integer resultCode) {
        this.resultCode = resultCode;
    }

    public static <T> ApiResponse<T> ok(Integer resultCode) {
        return new ApiResponse<>(resultCode);
    }

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(HttpStatus.OK.value(), data);
    }

    public static <T> ApiResponse<T> ok(Integer resultCode, T data) {
        return new ApiResponse<>(resultCode, data);
    }

    public static ApiResponse<String> error(HttpStatus httpStatus) {
        return new ApiResponse<>(httpStatus.value());
    }

    public static ApiResponse<String> error(HttpStatus httpStatus, String errorMessage) {
        return new ApiResponse<>(httpStatus.value(), errorMessage);
    }

}
