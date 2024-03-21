package com.example.miniproject.domain.hotel.dto;

import com.example.miniproject.domain.hotel.constant.ActiveStatus;
import com.example.miniproject.domain.hotel.constant.RoomType;
import com.example.miniproject.domain.hotel.entity.Room;
import com.example.miniproject.domain.hotel.entity.HotelThumbnail;
import com.example.miniproject.domain.hotel.entity.RoomThumbnail;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

public class RoomDTO {

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class Request {

        @NotNull(message = "객실 타입은 필수 입력입니다")
        private RoomType type;

        @NotNull(message = "판매 상태는 필수 입력입니다")
        private ActiveStatus activeStatus;

        @NotNull(message = "침대 수는 필수 입력입니다")
        private int bedCount;

        @NotNull(message = "기준 인원수는 필수 입력입니다")
        private int standardCapacity;

        @NotNull(message = "최대 인원수는 필수 입력입니다")
        private int maximumCapacity;

        @NotBlank(message = "뷰 타입은 필수 입력입니다")
        private String viewType;

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

        private int bedCount;

        private int standardCapacity;

        private int maximumCapacity;

        private String viewType;

        private BigDecimal standardPrice;

        private BigDecimal adultFare;

        private BigDecimal childFare;

        private BigDecimal discountRate;

        private List<RoomThumbnail> thumbnails;

        public static Response of(Room room) {
            return Response.builder()
              .hotelId(room.getHotel().getId())
              .id(room.getId())
              .type(room.getType())
              .activeStatus(room.getActiveStatus())
              .bedCount(room.getBedCount())
              .standardCapacity(room.getStandardCapacity())
              .maximumCapacity(room.getMaximumCapacity())
              .viewType(room.getViewType())
              .standardPrice(room.getStandardPrice())
              .adultFare(room.getAdultFare())
              .childFare(room.getChildFare())
              .thumbnails(room.getThumbnails())
              .build();
        }

    }

}
