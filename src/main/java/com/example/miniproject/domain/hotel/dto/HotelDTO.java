package com.example.miniproject.domain.hotel.dto;

import com.example.miniproject.domain.hotel.constant.ActiveStatus;
import com.example.miniproject.domain.hotel.constant.Nation;
import com.example.miniproject.domain.hotel.entity.BasicOptions;
import com.example.miniproject.domain.hotel.entity.Hotel;
import com.example.miniproject.domain.hotel.entity.Room;
import com.example.miniproject.domain.hotel.entity.Thumbnail;
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

        @NotBlank(message = "호텔 설명은 필수 입력입니다")
        private String description;

        @NotNull(message = "기본 옵션은 필수 입력입니다")
        private BasicOptions basicOptions;

        @NotNull(message = "판매 상태는 필수 입력입니다")
        private ActiveStatus activeStatus;

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

        private String description;

        private List<Thumbnail> thumbnails;

        private BasicOptions basicOptions;

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
              .description(hotel.getDescription())
              .thumbnails(hotel.getThumbnails())
              .basicOptions(hotel.getBasicOptions())
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
