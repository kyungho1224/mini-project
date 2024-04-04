package com.example.miniproject.domain.hotel.controller;

import com.example.miniproject.common.dto.ApiResponse;
import com.example.miniproject.domain.hotel.dto.NoticeDTO;
import com.example.miniproject.domain.hotel.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/hotels/{hotelId}/notices")
public class NoticeController {

    private final NoticeService noticeService;

    @PostMapping
    public ResponseEntity<ApiResponse<NoticeDTO.Response>> register(
      Authentication authentication,
      @PathVariable Long hotelId,
      @Validated
      @RequestBody NoticeDTO.Request request
    ) {
        NoticeDTO.Response response = noticeService.create(authentication.getName(), hotelId, request);
        return ResponseEntity.status(CREATED).body(ApiResponse.ok(response));
    }

    @PatchMapping("/{noticeId}")
    public ResponseEntity<ApiResponse<NoticeDTO.Response>> modify(
      Authentication authentication,
      @PathVariable Long hotelId,
      @PathVariable Long noticeId,
      @Validated
      @RequestBody NoticeDTO.Request request
    ) {
        NoticeDTO.Response response = noticeService.update(authentication.getName(), hotelId, noticeId, request);
        return ResponseEntity.status(ACCEPTED).body(ApiResponse.ok(response));
    }

    @DeleteMapping("/{noticeId}")
    public ResponseEntity<Void> unregister(
      Authentication authentication,
      @PathVariable Long hotelId,
      @PathVariable Long noticeId
    ) {
        noticeService.delete(authentication.getName(), hotelId, noticeId);
        return ResponseEntity.status(NO_CONTENT).build();
    }

}
