package com.example.miniproject.domain.order.entity;

import com.example.miniproject.common.entity.BaseEntity;
import com.example.miniproject.domain.hotel.entity.Room;
import com.example.miniproject.domain.member.entity.Member;
import com.example.miniproject.domain.order.constant.OrderStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

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
    @JoinColumn(nullable = false, name = "room_id")
    private Room room;

    @Column(nullable = false, columnDefinition = "DATETIME NOT NULL COMMENT '체크인 요청일'")
    private LocalDate checkIn;

    @Column(nullable = false, columnDefinition = "DATETIME NOT NULL COMMENT '체크아웃 요청일'")
    private LocalDate checkOut;

    @Column(columnDefinition = "int DEFAULT 0 COMMENT '어른 추가'")
    private int adultCount;

    @Column(columnDefinition = "int DEFAULT 0 COMMENT '아이 추가'")
    private int childCount;

    @Column(nullable = false, columnDefinition = "DECIMAL(11,4) NOT NULL COMMENT '결제 총액'")
    private BigDecimal totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(255) NOT NULL COMMENT '결제 상태'")
    private OrderStatus status;

    @Column(nullable = false, columnDefinition = "VARCHAR(255) NOT NULL COMMENT '요청 사항'")
    private String comment;

    @Column(columnDefinition = "VARCHAR(255) COMMENT '우편번호'")
    private String zipCode;

    @Column(columnDefinition = "VARCHAR(255) COMMENT '국가'")
    private String nation;

    @Column(columnDefinition = "VARCHAR(255) COMMENT '도시'")
    private String city;

    @Column(columnDefinition = "VARCHAR(255) COMMENT '주소'")
    private String address;

    public static Order saveAs(Member member, Room room, LocalDate checkIn, LocalDate checkOut,
                               int adultCount, int childCount, BigDecimal totalPrice) {
        return Order.builder()
          .member(member).room(room).checkIn(checkIn).checkOut(checkOut)
          .adultCount(adultCount).childCount(childCount).totalPrice(totalPrice)
          .build();
    }

    public void updateStatus(OrderStatus status) {
        this.status = status;
    }

    public void updateAdditionalInfo(String zipCode, String nation, String city, String address, String comment) {
        this.zipCode = zipCode;
        this.nation = nation;
        this.city = city;
        this.address = address;
        this.comment = comment;
    }

}
