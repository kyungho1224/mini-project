package com.example.miniproject.domain.hotel.dto;

import com.example.miniproject.domain.hotel.constant.RoomStatus;
import com.example.miniproject.domain.hotel.constant.RoomType;
import com.example.miniproject.domain.hotel.entity.Room;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

public class RoomDTO {

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class Request {

        @NotNull(message = "객실 타입은 필수 입력입니다")
        private RoomType type;

        @NotNull(message = "침대 수는 필수 입력입니다")
        private int bedCount;

        private int minCapacity;

        @NotNull(message = "최대 인원수는 필수 입력입니다")
        private int maxCapacity;

        @NotBlank(message = "뷰 타입은 필수 입력입니다")
        private String viewType;

        @NotNull(message = "가격은 필수 입력입니다")
        private BigDecimal price;

        private BigDecimal discountRate;

        private String imgUrl;

    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Getter
    public static class Response {

        private Long id;

        private RoomType type;

        private RoomStatus roomStatus;

        private int bedCount;

        private int minCapacity;

        private int maxCapacity;

        private String viewType;

        private BigDecimal price;

        private BigDecimal discountRate;

        private String imgUrl;

        public static Response of(Room room) {
            return Response.builder()
              .id(room.getId())
              .type(room.getType())
              .roomStatus(room.getRoomStatus())
              .bedCount(room.getBedCount())
              .minCapacity(room.getMinCapacity())
              .maxCapacity(room.getMaxCapacity())
              .viewType(room.getViewType())
              .price(room.getPrice())
              .discountRate(room.getDiscountRate())
              .imgUrl(room.getImgUrl())
              .build();
        }

    }

}
