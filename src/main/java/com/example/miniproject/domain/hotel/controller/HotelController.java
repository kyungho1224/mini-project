package com.example.miniproject.domain.hotel.controller;

import com.example.miniproject.common.dto.ApiResponse;
import com.example.miniproject.domain.hotel.constant.Nation;
import com.example.miniproject.domain.hotel.constant.RoomType;
import com.example.miniproject.domain.hotel.constant.ViewType;
import com.example.miniproject.domain.hotel.dto.HotelDTO;
import com.example.miniproject.domain.hotel.service.HotelService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static org.springframework.http.HttpStatus.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/hotels")
public class HotelController {

    private final HotelService hotelService;
    private final ObjectMapper objectMapper;

    @PostMapping
    public ResponseEntity<ApiResponse<HotelDTO.SimpleResponse>> register(
      Authentication authentication,
      @Validated
      @RequestParam(name = "request") String json,
      @RequestParam(name = "file", required = false) MultipartFile[] files
    ) {
        HotelDTO.Request request;
        try {
            request = objectMapper.readValue(json, HotelDTO.Request.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        HotelDTO.SimpleResponse response;
        if (files != null && files.length > 0) {
            response = hotelService.create(authentication.getName(), request, files);
        } else {
            response = hotelService.create(authentication.getName(), request);
        }
        return ResponseEntity.status(CREATED).body(ApiResponse.ok(response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<HotelDTO.Response>>> getAllVisibleHotels(
            Pageable pageable) {
        Page<HotelDTO.Response> hotelPage = hotelService.findAllVisibleHotels(pageable);
        return ResponseEntity.status(OK).body(ApiResponse.ok(hotelPage));
    }

    @GetMapping("/nation/{nation}")
    public ResponseEntity<ApiResponse<Page<HotelDTO.Response>>> getHotelsByNation(
            @PathVariable Nation nation, Pageable pageable) {
        Page<HotelDTO.Response> hotelPage = hotelService.findByNation(nation, pageable);
        return ResponseEntity.status(OK).body(ApiResponse.ok(hotelPage));
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<ApiResponse<Page<HotelDTO.Response>>> searchHotelsByName(
      @PathVariable String name, Pageable pageable) {
        Page<HotelDTO.Response> hotelPage = hotelService.findHotelsByNameAndVisible(name, pageable);
        return ResponseEntity.status(OK).body(ApiResponse.ok(hotelPage));
    }

    @GetMapping("/")
    public ResponseEntity<ApiResponse<Page<HotelDTO.Response>>> searchHotelsByNameAndNation(
      @RequestParam("name") String name,
      @RequestParam("nation") String nation,
      Pageable pageable) {

        Nation nationStr = Nation.valueOf(nation.toUpperCase()
          .replace("%", "").replace("\\b", ""));

        Page<HotelDTO.Response> hotelPage = hotelService.findHotelsByNameAndNationAndVisible(name, nationStr, pageable);
        return ResponseEntity.status(OK).body(ApiResponse.ok(hotelPage));
    }

    @GetMapping("/search/")
    public ResponseEntity<ApiResponse<Page<HotelDTO.Response>>> searchHotelsByNationAndType(
      @RequestParam("nation") String nation,
      @RequestParam("roomType") String roomType,
      @RequestParam("viewType") String viewType,
      Pageable pageable) {

        Nation nationStr = Nation.valueOf(nation.toUpperCase()
          .replace("%", "").replace("\\b", ""));
        RoomType roomTypeStr = RoomType.valueOf(roomType.toUpperCase()
          .replace("%", "").replace("\\b", ""));
        ViewType viewTypeStr = ViewType.valueOf(viewType.toUpperCase()
          .replace("%", "").replace("\\b", ""));

        Page<HotelDTO.Response> hotelPage = hotelService.findHotelsByNationAndTypeAndVisible(nationStr, roomTypeStr, viewTypeStr, pageable);
        return ResponseEntity.status(OK).body(ApiResponse.ok(hotelPage));
    }

    @GetMapping("/{hotelId}")
    public ResponseEntity<ApiResponse<HotelDTO.Response>> getAllVisibleRoomsByHotelId(@PathVariable Long hotelId) {
        return ResponseEntity.status(OK)
          .body(ApiResponse.ok(hotelService.findHotelById(hotelId)));
    }

    @PatchMapping("/{hotelId}")
    public ResponseEntity<Void> updateData(
      Authentication authentication,
      @PathVariable Long hotelId,
      @Validated
      @RequestBody HotelDTO.Request request
    ) {
        hotelService.updateData(authentication.getName(), hotelId, request);
        return ResponseEntity.status(NO_CONTENT).build();
    }

    @PatchMapping("/{hotelId}/thumbnails/{thumbnailId}")
    public ResponseEntity<Void> updateThumbnail(
      Authentication authentication,
      @PathVariable Long hotelId,
      @PathVariable Long thumbnailId,
      @RequestParam(name = "file", required = false) MultipartFile file
    ) {
        if (file != null) {
            hotelService.updateThumbnail(authentication.getName(), hotelId, thumbnailId, file);
        }
        return ResponseEntity.status(NO_CONTENT).build();
    }

    @DeleteMapping("/{hotelId}")
    public ResponseEntity<Void> unregister(
      Authentication authentication,
      @PathVariable Long hotelId
    ) {
        hotelService.unregister(authentication.getName(), hotelId);
        return ResponseEntity.status(NO_CONTENT).build();
    }

    @PostMapping("/{hotelId}/favorite")
    public ResponseEntity<Void> favorite(
      Authentication authentication,
      @PathVariable Long hotelId
    ) {
        hotelService.toggleFavorite(authentication.getName(), hotelId);
        return ResponseEntity.status(NO_CONTENT).build();
    }

}
