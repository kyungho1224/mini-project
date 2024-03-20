package com.example.miniproject.domain.hotel.entity;

import com.example.miniproject.common.entity.BaseEntity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Entity
@Table(name = "thumbnails")
public class Thumbnail extends BaseEntity {

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id")
    private Hotel hotel;

    private String img1;

    private String img2;

    private String img3;

    private String img4;

    public static Thumbnail saveAs(Hotel hotel, String img1, String img2, String img3, String img4) {
        return Thumbnail.builder()
          .hotel(hotel)
          .img1(img1)
          .img2(img2)
          .img3(img3)
          .img4(img4)
          .build();
    }

}
