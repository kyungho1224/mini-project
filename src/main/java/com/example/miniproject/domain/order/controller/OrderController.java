package com.example.miniproject.domain.order.controller;

import com.example.miniproject.common.dto.ApiResponse;
import com.example.miniproject.domain.order.dto.OrderDTO;
import com.example.miniproject.domain.order.entity.Order;
import com.example.miniproject.domain.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<ApiResponse<OrderDTO.OrderResponse>> createOrder(
      Authentication authentication,
      @RequestBody OrderDTO.OrderRequest orderRequest
    ) {
        Order order = orderService.createOrder(authentication.getName(), orderRequest);
        OrderDTO.OrderResponse orderResponse = OrderDTO.OrderResponse.of(order);
        return ResponseEntity.status(CREATED).body(ApiResponse.ok(orderResponse));
    }

    @PatchMapping("/{orderId}")
    public ResponseEntity<Void> updateOrderInfo(
      @PathVariable Long orderId,
      @RequestBody OrderDTO.OrderInfoRequest request) {
        orderService.updateOrderInfo(orderId, request);
        return ResponseEntity.status(NO_CONTENT).build();
    }

}
