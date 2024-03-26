package com.example.miniproject.domain.hotel.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ActiveStatus {

    ACTIVE("활성"),
    INACTIVE("비활성");

    private final String status;

}
