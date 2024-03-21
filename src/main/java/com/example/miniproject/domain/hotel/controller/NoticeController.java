package com.example.miniproject.domain.hotel.controller;

import com.example.miniproject.common.dto.ApiResponse;
import com.example.miniproject.domain.hotel.dto.NoticeDTO;
import com.example.miniproject.domain.hotel.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/hotels/{hotelId}/notices")
public class NoticeController {

    private final NoticeService noticeService;

    @PostMapping
    public ApiResponse<Void> register(
      Authentication authentication,
      @PathVariable Long hotelId,
      @Validated
      @RequestBody NoticeDTO.Request request
    ) {
        noticeService.create(authentication.getName(), hotelId, request);
        return ApiResponse.ok(HttpStatus.CREATED.value());
    }

    @PatchMapping("/{noticeId}")
    public ApiResponse<Void> modify(
      Authentication authentication,
      @PathVariable Long hotelId,
      @PathVariable Long noticeId,
      @Validated
      @RequestBody NoticeDTO.Request request
    ) {
        noticeService.update(authentication.getName(), hotelId, noticeId, request);
        return ApiResponse.ok(HttpStatus.CREATED.value());
    }

    @DeleteMapping("/{noticeId}")
    public ApiResponse<Void> unregister(
      Authentication authentication,
      @PathVariable Long hotelId,
      @PathVariable Long noticeId
    ) {
        noticeService.delete(authentication.getName(), hotelId, noticeId);
        return ApiResponse.ok(HttpStatus.NO_CONTENT.value());
    }

}
