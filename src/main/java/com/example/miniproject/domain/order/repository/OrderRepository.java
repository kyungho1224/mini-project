package com.example.miniproject.domain.order.repository;

import com.example.miniproject.domain.order.constant.OrderStatus;
import com.example.miniproject.domain.order.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Page<Order> findAllByMemberIdAndStatus(Long memberId, OrderStatus status, Pageable pageable);

    Optional<Order> findByIdAndMemberIdAndStatus(Long orderId, Long memberId, OrderStatus status);

}
