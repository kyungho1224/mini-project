package com.example.miniproject.domain.hotel.entity;

import com.example.miniproject.common.entity.BaseEntity;
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
@Table(name = "hotel_thumbnails")
public class HotelThumbnail extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "hotel_id")
    private Hotel hotel;

    private String imgUrl;

    public static HotelThumbnail saveAs(Hotel hotel, String imgUrl) {
        return new HotelThumbnail(hotel, imgUrl);
    }

    public void updateThumbnail(String imgUrl) {
        this.imgUrl = imgUrl;
    }

}
