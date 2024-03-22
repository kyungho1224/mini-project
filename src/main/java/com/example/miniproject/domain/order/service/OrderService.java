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

        // Room의 등록 상태, 판매 상태 체크
        if (room.getRegisterStatus() != RegisterStatus.VISIBLE || room.getActiveStatus() != ActiveStatus.ACTIVE) {
            throw new ApiException(ApiErrorCode.NOT_AVAILABLE_ROOM);
        }

        // 성인 인원 + 아이 인원이 추가할 수 있는 인원보다 크지 않은지 확인
        if (request.getAdultCount() + request.getChildCount() > room.getMaximumCapacity() - room.getStandardCapacity()) {
            throw new ApiException(ApiErrorCode.EXCEEDS_MAXIMUM_CAPACITY);
        }

        // 결제 총액 계산
        BigDecimal totalPrice = room.getStandardPrice()
                .add(room.getAdultFare().multiply(BigDecimal.valueOf(request.getAdultCount())))
                .add(room.getChildFare().multiply(BigDecimal.valueOf(request.getChildCount())));

        // 할인율 적용 시
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

        order.updateStatus(OrderStatus.PAYMENT_PENDING); // 결제 대기로 상태 변경

        return orderRepository.save(order);
    }

    public OrderDTO.OrderInfoResponse updateOrderInfo(
            Long orderId, OrderDTO.OrderInfoRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ApiException(ApiErrorCode.NOT_FOUND_ORDER));

        if (!order.getStatus().equals(OrderStatus.PAYMENT_PENDING)) {
            throw new ApiException(ApiErrorCode.NOT_FOUND_ORDER); // 결제 대기 상태가 아닌 경우의 예외코드 어떻게 해야할지 모르겠음
        }

        // 주소와 요청 사항 업데이트
        order.updateAdditionalInfo(
                request.getZipCode(),
                request.getNation(),
                request.getCity(),
                request.getAddress(),
                request.getComment());

        order.updateStatus(OrderStatus.PAYMENT_COMPLETED); // 결제 완료로 상태 변경

        orderRepository.save(order);

        return OrderDTO.OrderInfoResponse.of(order);
    }

}
