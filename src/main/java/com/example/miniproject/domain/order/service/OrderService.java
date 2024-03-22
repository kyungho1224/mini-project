package com.example.miniproject.domain.order.service;

import com.example.miniproject.domain.hotel.constant.ActiveStatus;
import com.example.miniproject.domain.hotel.constant.RegisterStatus;
import com.example.miniproject.domain.hotel.entity.Room;
import com.example.miniproject.domain.hotel.repository.RoomRepository;
import com.example.miniproject.domain.member.entity.Member;
import com.example.miniproject.domain.member.repository.MemberRepository;
import com.example.miniproject.domain.order.constant.OrderStatus;
import com.example.miniproject.domain.order.dto.OrderDTO;
import com.example.miniproject.domain.order.entity.Order;
import com.example.miniproject.domain.order.repository.OrderRepository;
import com.example.miniproject.exception.ApiErrorCode;
import com.example.miniproject.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Transactional
@Service
public class OrderService {

    private final OrderRepository orderRepository;

    private final RoomRepository roomRepository;

    private final MemberRepository memberRepository;

    public Order createOrder(OrderDTO.OrderRequest request) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(ApiErrorCode.NOT_FOUND_MEMBER));

        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new ApiException(ApiErrorCode.NOT_FOUND_ROOM));

        if (room.getRegisterStatus() != RegisterStatus.VISIBLE || room.getActiveStatus() != ActiveStatus.ACTIVE) {
            throw new ApiException(ApiErrorCode.NOT_AVAILABLE_ROOM);
        }

        if (request.getAdultCount() + request.getChildCount() > room.getMaximumCapacity() - room.getStandardCapacity()) {
            throw new ApiException(ApiErrorCode.EXCEEDS_MAXIMUM_CAPACITY);
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
                totalPrice,
                ""
        );

        order.updateStatus(OrderStatus.PAYMENT_PENDING);

        return orderRepository.save(order);
    }

    public OrderDTO.OrderInfoResponse updateOrderInfo(
            Long orderId, OrderDTO.OrderInfoRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ApiException(ApiErrorCode.NOT_FOUND_ORDER));

        if (!order.getStatus().equals(OrderStatus.PAYMENT_PENDING)) {
            throw new ApiException(ApiErrorCode.NOT_FOUND_ORDER);
        }

        order.updateAdditionalInfo(
                request.getZipCode(),
                request.getNation(),
                request.getCity(),
                request.getAddress(),
                request.getComment());

        order.updateStatus(OrderStatus.PAYMENT_COMPLETED);

        orderRepository.save(order);

        return OrderDTO.OrderInfoResponse.of(order);
    }

}
