package com.example.miniproject.domain.hotel.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum HotelStatus {

    VISIBLE("등록"),
    INVISIBLE("삭제"),
    ;

    private final String status;

}
