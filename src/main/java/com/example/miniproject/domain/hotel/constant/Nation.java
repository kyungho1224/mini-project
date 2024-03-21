package com.example.miniproject.domain.hotel.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Nation {

    THAILAND("태국"),
    VIETNAM("베트남"),
    PHILIPPINES("필리핀"),
    MALAYSIA("말레이시아"),
    TAIWAN("대만"),
    ;

    private final String name;

}
