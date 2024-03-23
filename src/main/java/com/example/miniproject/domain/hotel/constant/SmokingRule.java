package com.example.miniproject.domain.hotel.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum SmokingRule {

    TOTAL_IMPOSSIBLE("전객실 불가능"),
    SOME_POSSIBLE("일부객실 가능"),
    FULL_AVAILABLE("전체 가능");

    private final String description;

}
