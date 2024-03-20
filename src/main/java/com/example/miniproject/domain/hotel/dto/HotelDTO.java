package com.example.miniproject.domain.hotel.dto;

import com.example.miniproject.domain.hotel.constant.ActiveStatus;
import com.example.miniproject.domain.hotel.constant.HotelStatus;
import com.example.miniproject.domain.hotel.constant.Nation;
import com.example.miniproject.domain.hotel.entity.Hotel;
import com.example.miniproject.domain.hotel.entity.Room;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.List;

public class HotelDTO {

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class Request {

        @NotNull(message = "국가(지역)은 필수 입력입니다")
        private Nation nation;

        @NotBlank(message = "호텔명은 필수 입력입니다")
        private String name;

        private String imgUrl;

        @NotNull(message = "체크인 시간은 필수 입력입니다")
        private LocalTime checkIn;

        @NotNull(message = "체크아웃 시간은 필수 입력입니다")
        private LocalTime checkOut;

    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Getter
    public static class Response {

        private Long id;

        private Nation nation;

        private String name;

        private String imgUrl;

        private HotelStatus hotelStatus;

        private ActiveStatus activeStatus;

        private LocalTime checkIn;

        private LocalTime checkOut;

        private Long latitude;

        private Long longitude;

        private List<Room> rooms;

        public static Response of(Hotel hotel) {
            return Response.builder()
              .id(hotel.getId())
              .nation(hotel.getNation())
              .name(hotel.getName())
              .imgUrl(hotel.getImgUrl())
              .hotelStatus(hotel.getHotelStatus())
              .activeStatus(hotel.getActiveStatus())
              .checkIn(hotel.getCheckIn())
              .checkOut(hotel.getCheckOut())
              .latitude(hotel.getLatitude())
              .longitude(hotel.getLongitude())
              .rooms(hotel.getRooms())
              .build();
        }

    }

}
