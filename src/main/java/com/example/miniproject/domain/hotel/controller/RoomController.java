package com.example.miniproject.domain.hotel.controller;

import com.example.miniproject.common.dto.ApiResponse;
import com.example.miniproject.domain.hotel.dto.RoomDTO;
import com.example.miniproject.domain.hotel.entity.Room;
import com.example.miniproject.domain.hotel.service.RoomService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static org.springframework.http.HttpStatus.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/hotels/{hotelId}/rooms")
public class RoomController {

    private final RoomService roomService;
    private final ObjectMapper objectMapper;

    @PostMapping
    public ResponseEntity<RoomDTO.Response> register(
      Authentication authentication,
      @PathVariable Long hotelId,
      @Validated
      @RequestParam(name = "request") String json,
      @RequestParam(name = "file", required = false) MultipartFile[] files
    ) {
        RoomDTO.Request request;
        try {
            request = objectMapper.readValue(json, RoomDTO.Request.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        Room room;
        if (files != null && files.length > 0) {
            room = roomService.create(authentication.getName(), hotelId, request, files);
        } else {
            room = roomService.create(authentication.getName(), hotelId, request);
        }
        return ResponseEntity.status(CREATED).body(RoomDTO.Response.of(room));
    }

    @PatchMapping("/{roomId}")
    public ResponseEntity<ApiResponse<RoomDTO.Response>> modify(
      Authentication authentication,
      @PathVariable Long hotelId,
      @PathVariable Long roomId,
      @Validated
      @RequestParam(name = "request") String json,
      @RequestParam(name = "file", required = false) MultipartFile[] files
    ) {
        RoomDTO.Request request;
        try {
            request = objectMapper.readValue(json, RoomDTO.Request.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        Room room;
        if (files != null && files.length > 0) {
            room = roomService.update(authentication.getName(), hotelId, roomId, request, files);
        } else {
            room = roomService.update(authentication.getName(), hotelId, roomId, request);
        }
        return ResponseEntity.status(ACCEPTED).body(ApiResponse.ok(RoomDTO.Response.of(room)));
    }

    @DeleteMapping("/{roomId}")
    public ResponseEntity<Void> unregister(
      Authentication authentication,
      @PathVariable Long hotelId,
      @PathVariable Long roomId
    ) {
        roomService.unregister(authentication.getName(), hotelId, roomId);
        return ResponseEntity.status(NO_CONTENT).build();
    }

}
