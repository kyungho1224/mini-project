package com.example.miniproject.domain.hotel.entity;

import com.example.miniproject.common.entity.BaseEntity;
import com.example.miniproject.domain.hotel.constant.*;
import com.example.miniproject.domain.hotel.dto.BasicOptions;
import com.example.miniproject.domain.hotel.dto.HotelDTO;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalTime;
import java.util.ArrayList;
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
    @Builder.Default
    private List<HotelThumbnail> thumbnails = new ArrayList<>();

    @Column(nullable = false, columnDefinition = "TIME NOT NULL COMMENT '체크인'")
    private LocalTime checkIn;

    @Column(nullable = false, columnDefinition = "TIME NOT NULL COMMENT '체크아웃'")
    private LocalTime checkOut;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(255) NOT NULL COMMENT '흡연 규칙'")
    private SmokingRule smokingRule;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(255) NOT NULL COMMENT '애완동물 규칙'")
    private PetRule petRule;

    @Column(columnDefinition = "TIME DEFAULT NULL COMMENT '수영장 개장 시간'")
    private LocalTime poolOpeningTime;

    @Column(columnDefinition = "TIME DEFAULT NULL COMMENT '수영장 폐장 시간'")
    private LocalTime poolClosingTime;

    @JdbcTypeCode(SqlTypes.JSON)
    @JsonSubTypes.Type(JsonType.class)
    @Column(nullable = false, columnDefinition = "json")
    private BasicOptions basicOptions;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(255) NOT NULL COMMENT '판매 상태'")
    private ActiveStatus activeStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(255) NOT NULL COMMENT '등록 상태'")
    private RegisterStatus registerStatus;

    @Column(columnDefinition = "BIGINT COMMENT '위도'")
    private Long latitude;

    @Column(columnDefinition = "BIGINT COMMENT '경도'")
    private Long longitude;

    @JsonManagedReference
    @OneToMany(cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<Room> rooms;

    @OneToMany(cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<Favorite> favorites;

    @OneToMany(cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<Notice> notices;

    public static Hotel saveAs(HotelDTO.Request request) {
        return Hotel.builder()
          .nation(request.getNation())
          .name(request.getName())
          .description(request.getDescription())
          .basicOptions(request.getBasicOptions())
          .checkIn(request.getCheckIn())
          .checkOut(request.getCheckOut())
          .smokingRule(request.getSmokingRule())
          .petRule(request.getPetRule())
          .poolOpeningTime(request.getPoolOpeningTime())
          .poolClosingTime(request.getPoolClosingTime())
          .activeStatus(request.getActiveStatus())
          .registerStatus(RegisterStatus.VISIBLE)
          .build();
    }

    public void updateData(HotelDTO.Request request) {
        this.nation = request.getNation();
        this.name = request.getName();
        this.description = request.getDescription();
        this.basicOptions = request.getBasicOptions();
        this.checkIn = request.getCheckIn();
        this.checkOut = request.getCheckOut();
        this.smokingRule = request.getSmokingRule();
        this.petRule = request.getPetRule();
        this.poolOpeningTime = request.getPoolOpeningTime();
        this.poolClosingTime = request.getPoolClosingTime();
        this.activeStatus = request.getActiveStatus();
    }

    public void addRoom(Room room) {
        rooms.add(room);
    }

    public void addNotice(Notice notice) {
        notices.add(notice);
    }

    public void addFavorite(Favorite favorite) {
        favorites.add(favorite);
    }

    public void removeFavorite(Favorite favorite) {
        favorites.remove(favorite);
    }

    public void addThumbnail(HotelThumbnail thumbnail) {
        thumbnails.add(thumbnail);
    }

    public void delete() {
        this.registerStatus = RegisterStatus.INVISIBLE;
    }

}
