package com.example.miniproject.domain.member.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum MemberRole {

    USER("사용자"),
    ADMIN("관리자"),
    MASTER("마스터"),
    ;

    private final String status;

}
