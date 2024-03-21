package com.example.miniproject.domain.hotel.dto;


import com.example.miniproject.domain.hotel.entity.HotelThumbnail;
import com.example.miniproject.domain.hotel.entity.RoomThumbnail;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

public class ThumbnailDTO {

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class HotelThumbnailsResponse {

        Long id;

        String imgUrl;

        public static HotelThumbnailsResponse of(HotelThumbnail hotelThumbnail) {
            return new HotelThumbnailsResponse(hotelThumbnail.getId(), hotelThumbnail.getImgUrl());
        }

        public static List<HotelThumbnailsResponse> of(List<HotelThumbnail> hotelThumbnails) {
            return hotelThumbnails.stream().map(HotelThumbnailsResponse::of).collect(Collectors.toList());
        }

    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class RoomThumbnailsResponse {

        Long id;

        String imgUrl;

        public static RoomThumbnailsResponse of(RoomThumbnail roomThumbnail) {
            return new RoomThumbnailsResponse(roomThumbnail.getId(), roomThumbnail.getImgUrl());
        }

        public static List<RoomThumbnailsResponse> of(List<RoomThumbnail> roomThumbnails) {
            return roomThumbnails.stream().map(RoomThumbnailsResponse::of).collect(Collectors.toList());
        }
    }

}
