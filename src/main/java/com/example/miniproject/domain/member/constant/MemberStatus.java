package com.example.miniproject.domain.member.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum MemberStatus {

    NOT_CERTIFICATED("미인증"),
    CERTIFICATED("인증완료"),
    UNREGISTERED("탈퇴"),
    ;

    private final String message;

}
