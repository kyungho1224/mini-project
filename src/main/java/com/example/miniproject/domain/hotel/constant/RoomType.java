package com.example.miniproject.domain.hotel.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum RoomType {

    STANDARD("스탠다드 룸"),
    DELUXE("디럭스 룸"),
    TWIN("트윈 룸"),
    SWEET("스위트 룸"),
    PARTY("파티 룸"),
    VIP("VIP 룸");

    private final String type;

}
