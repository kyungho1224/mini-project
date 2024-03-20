package com.example.miniproject.domain.hotel.entity;

import com.example.miniproject.common.entity.BaseEntity;
import com.example.miniproject.domain.hotel.constant.ActiveStatus;
import com.example.miniproject.domain.hotel.constant.RegisterStatus;
import com.example.miniproject.domain.hotel.constant.RoomType;
import com.example.miniproject.domain.hotel.dto.RoomDTO;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

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
    @Column(nullable = false, columnDefinition = "VARCHAR(255) NOT NULL COMMENT '등록 상태'")
    private RegisterStatus registerStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(255) NOT NULL COMMENT '판매 상태'")
    private ActiveStatus activeStatus;

    @Column(nullable = false, columnDefinition = "int NOT NULL COMMENT '침대 수'")
    private int bedCount;

    @Column(nullable = false, columnDefinition = "int DEFAULT 0 COMMENT '표준 인원'")
    private int standardCapacity;

    @Column(nullable = false, columnDefinition = "int NOT NULL COMMENT '최대 인원'")
    private int maximumCapacity;

    @Column(nullable = false, columnDefinition = "VARCHAR(255) NOT NULL COMMENT '뷰 타입'")
    private String viewType;

    @Column(nullable = false, columnDefinition = "DECIMAL(11,4) NOT NULL COMMENT '표준 가격'")
    private BigDecimal standardPrice;

    @Column(nullable = false, columnDefinition = "DECIMAL(11,4) NOT NULL COMMENT '성인 요금'")
    private BigDecimal adultFare;

    @Column(nullable = false, columnDefinition = "DECIMAL(11,4) NOT NULL COMMENT '어린이 요금'")
    private BigDecimal childFare;

    @Column(columnDefinition = "DECIMAL(11,4) DEFAULT 0 COMMENT '할인율'")
    private BigDecimal discountRate;

    @OneToMany(cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<Thumbnail> thumbnails;

    public static Room saveAs(Hotel hotel, RoomDTO.Request request) {
        return Room.builder()
          .hotel(hotel)
          .type(request.getType())
          .activeStatus(request.getActiveStatus())
          .registerStatus(RegisterStatus.VISIBLE)
          .bedCount(request.getBedCount())
          .standardCapacity(request.getStandardCapacity())
          .maximumCapacity(request.getMaximumCapacity())
          .viewType(request.getViewType())
          .standardPrice(request.getStandardPrice())
          .adultFare(request.getAdultFare())
          .childFare(request.getChildFare())
          .build();
    }

    public void addThumbnail(Thumbnail thumbnail) {
        this.thumbnails.add(thumbnail);
    }

    public void removeThumbnail(Thumbnail thumbnail) {
        this.thumbnails.remove(thumbnail);
    }

}
