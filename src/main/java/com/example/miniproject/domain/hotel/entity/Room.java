package com.example.miniproject.domain.hotel.entity;

import com.example.miniproject.common.entity.BaseEntity;
import com.example.miniproject.domain.hotel.constant.RoomStatus;
import com.example.miniproject.domain.hotel.constant.RoomType;
import com.example.miniproject.domain.hotel.dto.RoomDTO;
import com.fasterxml.jackson.annotation.JsonBackReference;
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

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id")
    private Hotel hotel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(255) NOT NULL COMMENT '객실 타입'")
    private RoomType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(255) NOT NULL COMMENT '노출 상태'")
    private RoomStatus roomStatus;

    @Column(nullable = false, columnDefinition = "int NOT NULL COMMENT '침대 수'")
    private int bedCount;

    @Column(columnDefinition = "int DEFAULT 0 COMMENT '최소 인원'")
    private int minCapacity;

    @Column(name = "max_capacity", nullable = false, columnDefinition = "int NOT NULL COMMENT '최대 인원'")
    private int maxCapacity;

    @Column(nullable = false, columnDefinition = "VARCHAR(255) NOT NULL COMMENT '뷰 타입'")
    private String viewType;

    @Column(nullable = false, columnDefinition = "DECIMAL(11,4) NOT NULL COMMENT '가격'")
    private BigDecimal price;

    @Column(columnDefinition = "DECIMAL(11,4) DEFAULT 0 COMMENT '할인율'")
    private BigDecimal discountRate;

    @Column(columnDefinition = "VARCHAR(255) DEFAULT NULL COMMENT '객실 이미지'")
    private String imgUrl;

    public static Room saveAs(Hotel hotel, RoomDTO.Request request) {
        return Room.builder()
          .hotel(hotel)
          .type(request.getType())
          .roomStatus(RoomStatus.VISIBLE)
          .bedCount(request.getBedCount())
          .maxCapacity(request.getMaxCapacity())
          .viewType(request.getViewType())
          .price(request.getPrice())
          .build();
    }

    public void updateHotelStatus(RoomStatus roomStatus) {
        this.roomStatus = roomStatus;
    }

    public void updateThumbnail(String imgUrl) {
        this.imgUrl = imgUrl;
    }

}
