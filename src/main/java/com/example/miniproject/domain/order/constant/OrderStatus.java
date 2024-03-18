package com.example.miniproject.domain.order.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum OrderStatus {

    PAYMENT_PENDING("결제 대기"),
    PAYMENT_COMPLETED("결제 완료"),
    PAYMENT_CANCELED("결제 취소"),
    ;

    private final String status;

}
