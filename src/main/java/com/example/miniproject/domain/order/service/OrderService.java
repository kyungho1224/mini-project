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

    public Order createOrder(String email, OrderDTO.OrderRequest request) {
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

        return orderRepository.save(order);
    }

    public void updateOrderInfo(
      String email, Long orderId, OrderDTO.OrderInfoRequest request) {
        Order order = orderRepository.findById(orderId)
          .orElseThrow(() -> new ApiException(ApiErrorCode.NOT_FOUND_ORDER.getDescription()));

        // 결제 정보를 등록한 회원과 결제를 요청하는 회원이 동일한지 확인
        if (!order.getMember().getEmail().equals(email)) {
            throw new ApiException(ApiErrorCode.NOT_MATCH_MEMBER.getDescription());
        }

        // 회원의 보유 크레딧 확인
        Member member = order.getMember();
        BigDecimal memberCredit = member.getCredit();

        // 결제 금액이 회원의 보유 크레딧보다 많을 경우 예외 처리
        if (order.getTotalPrice().compareTo(memberCredit) > 0) {
            throw new ApiException(ApiErrorCode.LACK_CREDIT.getDescription());
        }

        // 결제 완료 및 크레딧 차감
        member.subtractCredit(order.getTotalPrice());
        memberService.updateMember(member); // 회원 정보 업데이트를 위한 메소드 호출. 구현 필요.

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
