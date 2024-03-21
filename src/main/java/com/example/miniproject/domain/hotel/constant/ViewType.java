package com.example.miniproject.domain.hotel.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ViewType {

    OCEAN("오션뷰"),
    CITY("씨티뷰"),
    GARDEN("가든뷰"),
    RIVER("리버뷰"),
    MOUNTAIN("마운틴뷰"),
    NONE("뷰 없음"),
    ;

    private final String type;

}
