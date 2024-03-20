package com.example.miniproject.domain.hotel.entity;

import com.example.miniproject.common.entity.BaseEntity;
import com.example.miniproject.domain.hotel.constant.ActiveStatus;
import com.example.miniproject.domain.hotel.constant.Nation;
import com.example.miniproject.domain.hotel.constant.RegisterStatus;
import com.example.miniproject.domain.hotel.dto.HotelDTO;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    @Column(nullable = false, columnDefinition = "VARCHAR(255) NOT NULL COMMENT '호텔 설명'")
    private String description;

    @OneToMany(cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<Thumbnail> thumbnails;

    @OneToOne(fetch = FetchType.LAZY)
    private BasicOptions basicOptions;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(255) NOT NULL COMMENT '등록 상태'")
    private RegisterStatus registerStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(255) NOT NULL COMMENT '판매 상태'")
    private ActiveStatus activeStatus;

    @Column(nullable = false, columnDefinition = "TIME NOT NULL COMMENT '체크인 시간'")
    private LocalTime checkIn;

    @Column(nullable = false, columnDefinition = "TIME NOT NULL COMMENT '체크아웃 시간'")
    private LocalTime checkOut;

    @Column(columnDefinition = "BIGINT COMMENT '위도'")
    private Long latitude;

    @Column(columnDefinition = "BIGINT COMMENT '경도'")
    private Long longitude;

    @JsonManagedReference
    @OneToMany(cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<Room> rooms;

    @OneToMany(cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<Favorite> favorites;

    public static Hotel saveAs(HotelDTO.Request request) {
        return Hotel.builder()
          .nation(request.getNation())
          .name(request.getName())
          .description(request.getDescription())
          .basicOptions(request.getBasicOptions())
          .activeStatus(ActiveStatus.ACTIVE)
          .registerStatus(RegisterStatus.VISIBLE)
          .checkIn(request.getCheckIn())
          .checkOut(request.getCheckOut())
          .build();
    }

    public void addRoom(Room room) {
        this.rooms.add(room);
    }

    public void removeRoom(Room room) {
        this.rooms.remove(room);
    }

    public void addFavorite(Favorite favorite) {
        this.favorites.add(favorite);
    }

    public void removeFavorite(Favorite favorite) {
        this.favorites.remove(favorite);
    }

    public void updateActiveStatus(ActiveStatus activeStatus) {
        this.activeStatus = activeStatus;
    }

    public void addThumbnail(Thumbnail thumbnail) {
        this.thumbnails.add(thumbnail);
    }

    public void removeThumbnail(Thumbnail thumbnail) {
        this.thumbnails.remove(thumbnail);
    }

}
