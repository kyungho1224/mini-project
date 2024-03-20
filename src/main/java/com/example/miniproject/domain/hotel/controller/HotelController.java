package com.example.miniproject.domain.hotel.controller;

import com.example.miniproject.common.dto.ApiResponse;
import com.example.miniproject.domain.hotel.dto.HotelDTO;
import com.example.miniproject.domain.hotel.service.HotelService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/hotels")
public class HotelController {

    private final HotelService hotelService;
    private final ObjectMapper objectMapper;

    @GetMapping("/welcome")
    public String welcome() {
        return "Welcome~";
    }

    @PostMapping
    public ApiResponse<String> register(
      Authentication authentication,
      @Validated
      @RequestParam(name = "request") String json,
      @RequestParam(name = "file", required = false) MultipartFile file
    ) {

        HotelDTO.Request request;
        try {
            request = objectMapper.readValue(json, HotelDTO.Request.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        if (file != null && !file.isEmpty()) {
            hotelService.create(authentication.getName(), request, file);
        } else {
            hotelService.create(authentication.getName(), request);
        }
        return ApiResponse.ok(HttpStatus.CREATED.value(), "Registered successfully");
    }

    @PostMapping("/{hotelId}/upload")
    public ApiResponse<String> upload(
      Authentication authentication,
      @PathVariable Long hotelId,
      @RequestParam(name = "file") MultipartFile file
    ) {
        hotelService.uploadThumbnail(authentication.getName(), hotelId, file);
        return ApiResponse.ok(HttpStatus.OK.value(), "Thumbnail upload successfully");
    }

}
