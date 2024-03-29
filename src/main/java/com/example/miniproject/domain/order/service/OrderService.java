package com.example.miniproject.domain.order.service;

import com.example.miniproject.domain.hotel.entity.Room;
import com.example.miniproject.domain.hotel.service.RoomService;
import com.example.miniproject.domain.member.entity.Member;
import com.example.miniproject.domain.member.service.MemberService;
import com.example.miniproject.domain.order.constant.OrderStatus;
import com.example.miniproject.domain.order.dto.OrderDTO;
import com.example.miniproject.domain.order.entity.Order;
import com.example.miniproject.domain.order.repository.OrderRepository;
import com.example.miniproject.exception.ApiErrorCode;
import com.example.miniproject.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Transactional
@Service
public class OrderService {

    private final MemberService memberService;
    private final RoomService roomService;
    private final OrderRepository orderRepository;

//    public Order createOrder(String email, OrderDTO.OrderRequest request) {
//        Member member = memberService.getValidMemberOrThrow(email);
//        Room room = roomService.getVisibleAndActiveRoomOrThrow(request.getRoomId());
//
//        if (request.getAdultCount() + request.getChildCount() > room.getMaximumCapacity() - room.getStandardCapacity()) {
//            throw new ApiException(ApiErrorCode.EXCEEDS_MAXIMUM_CAPACITY.getDescription());
//        }
//
//        BigDecimal totalPrice = room.getStandardPrice()
//          .add(room.getAdultFare().multiply(BigDecimal.valueOf(request.getAdultCount())))
//          .add(room.getChildFare().multiply(BigDecimal.valueOf(request.getChildCount())));
//
//        if (room.getDiscountRate() != null) {
//            totalPrice = totalPrice.multiply(BigDecimal.ONE.subtract(room.getDiscountRate()));
//        }
//
//        Order order = Order.saveAs(
//          member,
//          room,
//          request.getCheckIn(),
//          request.getCheckOut(),
//          request.getAdultCount(),
//          request.getChildCount(),
//          totalPrice
//        );
//
//        order.updateStatus(OrderStatus.PAYMENT_PENDING);
//
//        return orderRepository.save(order);
//    }

    public OrderDTO.OrderResponse createOrder(String email, OrderDTO.OrderRequest request) {
        Member member = memberService.getValidMemberOrThrow(email);
        Room room = roomService.getVisibleAndActiveRoomOrThrow(request.getRoomId());

        if (request.getAdultCount() + request.getChildCount() > room.getMaximumCapacity() - room.getStandardCapacity()) {
            throw new ApiException(ApiErrorCode.EXCEEDS_MAXIMUM_CAPACITY.getDescription());
        }

        BigDecimal totalPrice = room.getStandardPrice()
                .add(room.getAdultFare().multiply(BigDecimal.valueOf(request.getAdultCount())))
                .add(room.getChildFare().multiply(BigDecimal.valueOf(request.getChildCount())));

        if (room.getDiscountRate() != null) {
            totalPrice = totalPrice.multiply(BigDecimal.ONE.subtract(room.getDiscountRate()));
        }

        Order order = Order.saveAs(
                member,
                room,
                request.getCheckIn(),
                request.getCheckOut(),
                request.getAdultCount(),
                request.getChildCount(),
                totalPrice
        );

        order.updateStatus(OrderStatus.PAYMENT_PENDING);

        return OrderDTO.OrderResponse.of(orderRepository.save(order));
    }

    public void updateOrderInfo(
      String email, Long orderId, OrderDTO.OrderInfoRequest request) {
        Order order = orderRepository.findById(orderId)
          .orElseThrow(() -> new ApiException(ApiErrorCode.NOT_FOUND_ORDER.getDescription()));

        if (!order.getMember().getEmail().equals(email)) {
            throw new ApiException(ApiErrorCode.NOT_MATCH_MEMBER.getDescription());
        }

        Member member = order.getMember();
        BigDecimal memberCredit = member.getCredit();

        if (order.getTotalPrice().compareTo(memberCredit) > 0) {
            throw new ApiException(ApiErrorCode.LACK_CREDIT.getDescription());
        }

        member.subtractCredit(order.getTotalPrice());
        member.updateAdditionalInfo(
            request.getZipCode(),
            request.getNation(),
            request.getCity(),
            request.getAddress()
        );
        memberService.updateMember(member);

        if (!order.getStatus().equals(OrderStatus.PAYMENT_PENDING)) {
            throw new ApiException(ApiErrorCode.NOT_FOUND_ORDER.getDescription());
        }

        order.updateAdditionalInfo(
          request.getZipCode(),
          request.getNation(),
          request.getCity(),
          request.getAddress(),
          request.getComment());

        order.updateStatus(OrderStatus.PAYMENT_COMPLETED);
    }

    public Page<OrderDTO.OrderDetailResponse> orderList(String email, Pageable pageable) {
        memberService.getMasterMemberOrThrow(email);
        return orderRepository.findAll(pageable).map(OrderDTO.OrderDetailResponse::of);
    }

}
