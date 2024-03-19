package com.example.miniproject.domain.hotel.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Nation {

    KOREA("한국"),
    JAPAN("일본"),
    CHINA("중국"),
    ;

    private final String name;

}
