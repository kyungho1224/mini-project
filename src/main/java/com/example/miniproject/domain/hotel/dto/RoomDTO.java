package com.example.miniproject.domain.hotel.dto;

import com.example.miniproject.domain.hotel.constant.ActiveStatus;
import com.example.miniproject.domain.hotel.constant.BedType;
import com.example.miniproject.domain.hotel.constant.RoomType;
import com.example.miniproject.domain.hotel.constant.ViewType;
import com.example.miniproject.domain.hotel.entity.Room;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public class RoomDTO {

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class Request {

        @NotNull(message = "객실 타입은 필수 입력입니다")
        private RoomType type;

        @NotNull(message = "판매 상태는 필수 입력입니다")
        private ActiveStatus activeStatus;

        @NotNull(message = "침대 타입은 필수 입력입니다")
        private BedType bedType;

        @NotNull(message = "기준 인원수는 필수 입력입니다")
        private int standardCapacity;

        @NotNull(message = "최대 인원수는 필수 입력입니다")
        private int maximumCapacity;

        @NotNull(message = "뷰 타입은 필수 입력입니다")
        private ViewType viewType;

        @NotNull(message = "표준 가격은 필수 입력입니다")
        private BigDecimal standardPrice;

        @NotNull(message = "성인 요금은 필수 입력입니다")
        private BigDecimal adultFare;

        @NotNull(message = "어린이 요금은 필수 입력입니다")
        private BigDecimal childFare;

        private BigDecimal discountRate;

    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Getter
    public static class Response {

        private Long hotelId;

        private Long id;

        private RoomType type;

        private ActiveStatus activeStatus;

        private BedType bedType;

        private int standardCapacity;

        private int maximumCapacity;

        private ViewType viewType;

        private BigDecimal standardPrice;

        private BigDecimal adultFare;

        private BigDecimal childFare;

        private BigDecimal discountRate;

        private List<ThumbnailDTO.RoomThumbnailsResponse> thumbnails;

        public static Response of(Room room) {
            return Response.builder()
              .hotelId(room.getHotel().getId())
              .id(room.getId())
              .type(room.getType())
              .activeStatus(room.getActiveStatus())
              .bedType(room.getBedType())
              .standardCapacity(room.getStandardCapacity())
              .maximumCapacity(room.getMaximumCapacity())
              .viewType(room.getViewType())
              .standardPrice(room.getStandardPrice())
              .adultFare(room.getAdultFare())
              .childFare(room.getChildFare())
              .thumbnails(ThumbnailDTO.RoomThumbnailsResponse.of(room.getThumbnails()))
              .build();
        }

        public static List<Response> of(List<Room> rooms) {
            return rooms.stream().map(Response::of).collect(Collectors.toList());
        }

    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Getter
    public static class SimpleResponse {

        private Long id;

        private RoomType type;

        private BedType bedType;

        private ViewType viewType;

        public static SimpleResponse of(Room room) {
            return SimpleResponse.builder()
              .id(room.getId())
              .type(room.getType())
              .bedType(room.getBedType())
              .viewType(room.getViewType())
              .build();
        }

    }

}
