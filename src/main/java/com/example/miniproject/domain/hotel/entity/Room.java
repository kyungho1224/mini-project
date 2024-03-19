package com.example.miniproject.domain.hotel.entity;

import com.example.miniproject.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Entity
@Table(name = "rooms", indexes = {
  @Index(name = "max_capacity_idx", columnList = "max_capacity")
})
public class Room extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "hotel_id")
    private Hotel hotel;

    @Column(nullable = false, columnDefinition = "int NOT NULL COMMENT '침대 수'")
    private int bedCount;

    @Column(columnDefinition = "int DEFAULT 0 COMMENT '최소 인원'")
    private int minCapacity;

    @Column(nullable = false, columnDefinition = "int NOT NULL COMMENT '최대 인원'")
    private int maxCapacity;

    @Column(nullable = false, columnDefinition = "VARCHAR(255)(20) NOT NULL COMMENT '뷰 타입'")
    private String viewType;

    @Column(nullable = false, columnDefinition = "DECIMAL(11,4) NOT NULL COMMENT '가격'")
    private BigDecimal price;

    @Column(columnDefinition = "DECIMAL(11,4) DEFAULT 0 COMMENT '할인율'")
    private BigDecimal discountRate;

    @Column(columnDefinition = "VARCHAR(255) DEFAULT NULL COMMENT '객실 이미지'")
    private String imgUrl;

    public static Room saveAs(Hotel hotel, int bedCount, int maxCapacity, String viewType, BigDecimal price) {
        return Room.builder()
          .hotel(hotel)
          .bedCount(bedCount)
          .maxCapacity(maxCapacity)
          .viewType(viewType)
          .price(price)
          .build();
    }

    public void updateThumbnail(String imgUrl) {
        this.imgUrl = imgUrl;
    }

}
