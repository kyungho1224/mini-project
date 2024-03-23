package com.example.miniproject.domain.hotel.dto;

import com.example.miniproject.domain.hotel.constant.*;
import com.example.miniproject.domain.hotel.entity.Hotel;
import com.example.miniproject.domain.hotel.entity.Notice;
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

        @NotBlank(message = "호텔 설명은 필수 입력입니다")
        private String description;

        @NotNull(message = "기본 옵션은 필수 입력입니다")
        private BasicOptions basicOptions;

        @NotNull(message = "체크인 시간은 필수 입력입니다")
        private LocalTime checkIn;

        @NotNull(message = "체크아웃 시간은 필수 입력입니다")
        private LocalTime checkOut;

        @NotNull(message = "흡연 규칙은 필수 입력입니다")
        private SmokingRule smokingRule;

        @NotNull(message = "애완동물 규칙은 필수 입력입니다")
        private PetRule petRule;

        private LocalTime poolOpeningTime;

        private LocalTime poolClosingTime;

        @NotNull(message = "판매 상태는 필수 입력입니다")
        private ActiveStatus activeStatus;

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

        private List<ThumbnailDTO.HotelThumbnailsResponse> thumbnails;

        private List<NoticeDTO.Response> notices;

        private BasicOptions basicOptions;

        private LocalTime checkIn;

        private LocalTime checkOut;

        private SmokingRule smokingRule;

        private PetRule petRule;

        private LocalTime poolOpeningTime;

        private LocalTime poolClosingTime;

        private ActiveStatus activeStatus;

        private Long latitude;

        private Long longitude;

        private List<RoomDTO.Response> rooms;

        public static Response of(Hotel hotel) {

            List<Room> rooms = hotel.getRooms().stream()
              .filter(room -> room.getRegisterStatus() == RegisterStatus.VISIBLE)
              .toList();

            List<Notice> notices = hotel.getNotices().stream()
              .filter(notice -> notice.getRegisterStatus() == RegisterStatus.VISIBLE)
              .toList();

            return Response.builder()
              .id(hotel.getId())
              .nation(hotel.getNation())
              .name(hotel.getName())
              .description(hotel.getDescription())
              .thumbnails(ThumbnailDTO.HotelThumbnailsResponse.of(hotel.getThumbnails()))
              .notices(NoticeDTO.Response.of(notices))
              .basicOptions(hotel.getBasicOptions())
              .checkIn(hotel.getCheckIn())
              .checkOut(hotel.getCheckOut())
              .smokingRule(hotel.getSmokingRule())
              .petRule(hotel.getPetRule())
              .poolOpeningTime(hotel.getPoolOpeningTime())
              .poolClosingTime(hotel.getPoolClosingTime())
              .activeStatus(hotel.getActiveStatus())
              .latitude(hotel.getLatitude())
              .longitude(hotel.getLongitude())
              .rooms(RoomDTO.Response.of(rooms))
              .build();
        }

    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Getter
    public static class SimpleResponse {

        private Long id;

        private Nation nation;

        private String name;

        private LocalTime checkIn;

        private LocalTime checkOut;

        public static SimpleResponse of(Hotel hotel) {
            return SimpleResponse.builder()
              .id(hotel.getId())
              .nation(hotel.getNation())
              .name(hotel.getName())
              .checkIn(hotel.getCheckIn())
              .checkOut(hotel.getCheckOut())
              .build();
        }

    }

}
