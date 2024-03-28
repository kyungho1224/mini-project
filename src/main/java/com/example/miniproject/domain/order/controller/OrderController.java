package com.example.miniproject.domain.order.controller;

import com.example.miniproject.common.dto.ApiResponse;
import com.example.miniproject.domain.order.dto.OrderDTO;
import com.example.miniproject.domain.order.entity.Order;
import com.example.miniproject.domain.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

//    @PostMapping
//    public ResponseEntity<ApiResponse<OrderDTO.OrderResponse>> createOrder(
//      Authentication authentication,
//      @RequestBody OrderDTO.OrderRequest orderRequest
//    ) {
//        Order order = orderService.createOrder(authentication.getName(), orderRequest);
//        OrderDTO.OrderResponse orderResponse = OrderDTO.OrderResponse.of(order);
//        return ResponseEntity.status(CREATED).body(ApiResponse.ok(orderResponse));
//    }

    @PostMapping
    public ResponseEntity<ApiResponse<OrderDTO.OrderResponse>> createOrder(
            Authentication authentication,
            @RequestBody OrderDTO.OrderRequest orderRequest
    ) {
        OrderDTO.OrderResponse orderResponse = orderService.createOrder(authentication.getName(), orderRequest);
        return ResponseEntity.status(CREATED).body(ApiResponse.ok(orderResponse));
    }

    @PatchMapping("/{orderId}")
    public ResponseEntity<Void> updateOrderInfo(
      Authentication authentication,
      @PathVariable Long orderId,
      @RequestBody OrderDTO.OrderInfoRequest request) {
        orderService.updateOrderInfo(authentication.getName(), orderId, request);
        return ResponseEntity.status(NO_CONTENT).build();
    }

    @GetMapping("/order-list")
    public ResponseEntity<ApiResponse<Page<OrderDTO.OrderDetailResponse>>> orderList(
      Authentication authentication,
      Pageable pageable
    ) {
        var result = orderService.orderList(authentication.getName(), pageable);
        return ResponseEntity.status(OK).body(ApiResponse.ok(result));
    }

}
