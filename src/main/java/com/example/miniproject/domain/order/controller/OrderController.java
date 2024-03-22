package com.example.miniproject.domain.order.controller;

import com.example.miniproject.common.dto.ApiResponse;
import com.example.miniproject.domain.member.service.MemberService;
import com.example.miniproject.domain.order.dto.OrderDTO;
import com.example.miniproject.domain.order.entity.Order;
import com.example.miniproject.domain.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/order")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ApiResponse<OrderDTO.OrderResponse> createOrder(@RequestBody OrderDTO.OrderRequest orderRequest) {
        Order order = orderService.createOrder(orderRequest);
        OrderDTO.OrderResponse orderResponse = OrderDTO.OrderResponse.of(order);
        return ApiResponse.ok(HttpStatus.CREATED.value(), orderResponse);
    }

}
