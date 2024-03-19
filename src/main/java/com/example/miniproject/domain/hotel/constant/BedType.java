package com.example.miniproject.domain.hotel.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum BedType {

    SINGLE("싱글"),
    DOUBLE("더블"),
    KING("킹 사이즈"),
    QUEEN("퀸 사이즈"),
    SOFA_BED("소파 베드"),
    BUNK_BED("이층 침대"),
    STORAGE_BED("수납 침대"),
    CUSTOM("맞춤형"),
    ;

    private final String type;

}
