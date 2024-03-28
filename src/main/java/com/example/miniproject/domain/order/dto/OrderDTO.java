package com.example.miniproject.domain.order.dto;

import com.example.miniproject.domain.hotel.dto.HotelDTO;
import com.example.miniproject.domain.hotel.dto.RoomDTO;
import com.example.miniproject.domain.member.dto.MemberDTO;
import com.example.miniproject.domain.order.constant.OrderStatus;
import com.example.miniproject.domain.order.entity.Order;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

public class OrderDTO {

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Builder
    public static class OrderRequest {

        @NotNull
        private Long roomId;

        @NotNull(message = "체크인 날짜는 필수 입력입니다")
        private LocalDate checkIn;

        @NotNull(message = "체크아웃 날짜는 필수 입력입니다")
        private LocalDate checkOut;

        private int adultCount;

        private int childCount;

        private BigDecimal totalPrice;

    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Getter
    public static class OrderResponse {
        private Long id;
        private Long memberId;
        private Long roomId;
        private LocalDate checkIn;
        private LocalDate checkOut;
        private int adultCount;
        private int childCount;
        private BigDecimal totalPrice;

        public static OrderResponse of(Order order) {
            return OrderResponse.builder()
              .id(order.getId())
              .memberId(order.getMember().getId())
              .roomId(order.getRoom().getId())
              .checkIn(order.getCheckIn())
              .checkOut(order.getCheckOut())
              .adultCount(order.getAdultCount())
              .childCount(order.getChildCount())
              .totalPrice(order.getTotalPrice())
              .build();
        }
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Builder
    public static class OrderInfoRequest {

        @NotNull(message = "우편번호 필수 입력입니다")
        private String zipCode;

        @NotNull(message = "국가 필수 입력입니다")
        private String nation;

        @NotNull(message = "도시 필수 입력입니다")
        private String city;

        @NotNull(message = "주소 필수 입력입니다")
        private String address;

        private String comment;

    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Getter
    public static class OrderInfoResponse {
        private Long id;
        private Long memberId;
        private Long roomId;
        private LocalDate checkIn;
        private LocalDate checkOut;
        private int adultCount;
        private int childCount;
        private BigDecimal totalPrice;
        private OrderStatus status;
        private String address;
        private String city;
        private String nation;
        private String zipCode;
        private String comment;

        public static OrderInfoResponse of(Order order) {
            return OrderInfoResponse.builder()
              .id(order.getId())
              .memberId(order.getMember().getId())
              .roomId(order.getRoom().getId())
              .checkIn(order.getCheckIn())
              .checkOut(order.getCheckOut())
              .adultCount(order.getAdultCount())
              .childCount(order.getChildCount())
              .totalPrice(order.getTotalPrice())
              .status(order.getStatus())
              .address(order.getAddress())
              .city(order.getCity())
              .nation(order.getNation())
              .zipCode(order.getZipCode())
              .comment(order.getComment())
              .build();
        }
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Getter
    public static class OrderDetailResponse {
        private Long id;
        private MemberDTO.SimpleResponse member;
        private HotelDTO.SimpleResponse hotel;
        private RoomDTO.SimpleResponse room;
        private LocalDate checkIn;
        private LocalDate checkOut;
        private int adultCount;
        private int childCount;
        private BigDecimal totalPrice;
        private OrderStatus status;
        private String address;
        private String city;
        private String nation;
        private String zipCode;
        private String comment;

        public static OrderDetailResponse of(Order order) {
            return OrderDetailResponse.builder()
              .id(order.getId())
              .member(MemberDTO.SimpleResponse.of(order.getMember()))
              .hotel(HotelDTO.SimpleResponse.of(order.getRoom().getHotel()))
              .room(RoomDTO.SimpleResponse.of(order.getRoom()))
              .checkIn(order.getCheckIn())
              .checkOut(order.getCheckOut())
              .adultCount(order.getAdultCount())
              .childCount(order.getChildCount())
              .totalPrice(order.getTotalPrice())
              .status(order.getStatus())
              .zipCode(order.getZipCode())
              .nation(order.getNation())
              .city(order.getCity())
              .address(order.getAddress())
              .comment(order.getComment())
              .build();
        }
    }

}
