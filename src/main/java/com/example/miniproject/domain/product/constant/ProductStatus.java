package com.example.miniproject.domain.product.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ProductStatus {

    ACTIVE("활성"),
    INACTIVE("비활성"),
    ;

    private final String status;

}
