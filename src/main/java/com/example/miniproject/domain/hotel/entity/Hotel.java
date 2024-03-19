package com.example.miniproject.domain.hotel.entity;

import com.example.miniproject.common.entity.BaseEntity;
import com.example.miniproject.domain.hotel.constant.BedType;
import com.example.miniproject.domain.hotel.constant.Nation;
import com.example.miniproject.domain.hotel.constant.ActiveStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Entity
@Table(name = "hotels", indexes = {
  @Index(name = "nation_idx", columnList = "nation")
})
public class Hotel extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(255) NOT NULL COMMENT '국가'")
    private Nation nation;

    @Column(nullable = false, columnDefinition = "VARCHAR(255) NOT NULL COMMENT '호텔명'")
    private String name;

    @Column(columnDefinition = "VARCHAR(255) DEFAULT NULL COMMENT '호텔 이미지'")
    private String imgUrl;

    // 편의시설

    // 객실규칙

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(255) NOT NULL COMMENT '판매 상태'")
    private ActiveStatus status;

    @Column(nullable = false, columnDefinition = "DATETIME NOT NULL COMMENT '체크인 시간'")
    private LocalTime checkIn;

    @Column(nullable = false, columnDefinition = "DATETIME NOT NULL COMMENT '체크아웃 시간'")
    private LocalTime checkOut;

    @Column(nullable = false, columnDefinition = "BIGINT DEFAULT 0 COMMENT '위도'")
    private Long latitude;

    @Column(nullable = false, columnDefinition = "BIGINT DEFAULT 0 COMMENT '경도'")
    private Long longitude;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Room> rooms;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Favorite> favorites;

    public static Hotel saveAs(Nation nation, String name, LocalTime checkIn, LocalTime checkOut) {
        return Hotel.builder()
          .nation(nation)
          .name(name)
          .status(ActiveStatus.ACTIVE)
          .checkIn(checkIn)
          .checkOut(checkOut)
          .build();
    }

    public void updateStatus(ActiveStatus status) {
        this.status = status;
    }

    public void updateThumbnail(String imgUrl) {
        this.imgUrl = imgUrl;
    }

}
