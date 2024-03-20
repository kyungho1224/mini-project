package com.example.miniproject.domain.hotel.controller;

import com.example.miniproject.common.dto.ApiResponse;
import com.example.miniproject.domain.hotel.dto.RoomDTO;
import com.example.miniproject.domain.hotel.service.RoomService;
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
@RequestMapping("/api/hotels/{hotelId}/rooms")
public class RoomController {

    private final RoomService roomService;
    private final ObjectMapper objectMapper;

    @PostMapping
    public ApiResponse<String> register(
      Authentication authentication,
      @PathVariable Long hotelId,
      @Validated
      @RequestParam(name = "request") String json,
      @RequestParam(name = "file", required = false) MultipartFile file
    ) {

        RoomDTO.Request request;
        try {
            request = objectMapper.readValue(json, RoomDTO.Request.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        if (file != null && !file.isEmpty()) {
            roomService.create(authentication.getName(), hotelId, request, file);
        } else {
            roomService.create(authentication.getName(), hotelId, request);
        }

        return ApiResponse.ok(HttpStatus.CREATED.value(), "Registered successfully");
    }

    @PostMapping("/{roomId}/upload")
    public ApiResponse<String> upload(
      Authentication authentication,
      @PathVariable Long hotelId,
      @PathVariable Long roomId,
      @RequestParam(name = "file") MultipartFile file
    ) {
        roomService.uploadThumbnail(authentication.getName(), hotelId, roomId, file);
        return ApiResponse.ok(HttpStatus.OK.value(), "Thumbnail upload successfully");
    }

}
