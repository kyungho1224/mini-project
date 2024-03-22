package com.example.miniproject.domain.hotel.controller;

import com.example.miniproject.domain.hotel.dto.RoomDTO;
import com.example.miniproject.domain.hotel.service.RoomService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/hotels/{hotelId}/rooms")
public class RoomController {

    private final RoomService roomService;
    private final ObjectMapper objectMapper;

    @PostMapping
    public ResponseEntity<Void> register(
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

        if (files != null && files.length > 0) {
            roomService.create(authentication.getName(), hotelId, request, files);
        } else {
            roomService.create(authentication.getName(), hotelId, request);
        }
        return ResponseEntity.status(CREATED).build();
    }

    @PatchMapping("/{roomId}")
    public ResponseEntity<Void> updateData(
      Authentication authentication,
      @PathVariable Long hotelId,
      @PathVariable Long roomId,
      @Validated
      @RequestBody RoomDTO.Request request
    ) {
        roomService.updateData(authentication.getName(), hotelId, roomId, request);
        return ResponseEntity.status(NO_CONTENT).build();
    }

    @PatchMapping("/{roomId}/thumbnails/{thumbnailId}")
    public ResponseEntity<Void> updateThumbnail(
      Authentication authentication,
      @PathVariable Long hotelId,
      @PathVariable Long roomId,
      @PathVariable Long thumbnailId,
      @RequestParam(name = "file") MultipartFile file
    ) {
        Optional.ofNullable(file)
          .ifPresent(image ->
            roomService.updateThumbnail(authentication.getName(), hotelId, roomId, thumbnailId, image));
        return ResponseEntity.status(NO_CONTENT).build();
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
