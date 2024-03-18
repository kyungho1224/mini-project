package com.example.miniproject.domain.order.entity;

import com.example.miniproject.common.entity.BaseEntity;
import com.example.miniproject.domain.member.entity.Member;
import com.example.miniproject.domain.order.constant.OrderStatus;
import com.example.miniproject.domain.product.entity.Product;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Entity
@Table(name = "orders")
public class Order extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "product_id")
    private Product product;

    @Column(nullable = false, columnDefinition = "DATETIME NOT NULL COMMENT '체크인'")
    private LocalDateTime checkIn;

    @Column(nullable = false, columnDefinition = "DATETIME NOT NULL COMMENT '체크아웃'")
    private LocalDateTime checkOut;

    @Column(nullable = false, columnDefinition = "int NOT NULL COMMENT '어른 추가'")
    private int adult;

    @Column(nullable = false, columnDefinition = "int NOT NULL COMMENT '아이 추가'")
    private int child;

    @Column(nullable = false, columnDefinition = "DECIMAL(11,4) NOT NULL COMMENT '결제 총액'")
    private BigDecimal totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(20) NOT NULL COMMENT '결제 상태'")
    private OrderStatus status;

}
