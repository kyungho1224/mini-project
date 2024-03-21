package com.example.miniproject.domain.hotel.controller;

import com.example.miniproject.common.dto.ApiResponse;
import com.example.miniproject.domain.hotel.constant.Nation;
import com.example.miniproject.domain.hotel.dto.HotelDTO;
import com.example.miniproject.domain.hotel.entity.Hotel;
import com.example.miniproject.domain.hotel.service.HotelService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/hotels")
public class HotelController {

    private final HotelService hotelService;
    private final ObjectMapper objectMapper;

    @PostMapping
    public ApiResponse<String> register(
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

        if (files != null && files.length > 0) {
            hotelService.create(authentication.getName(), request, files);
        } else {
            hotelService.create(authentication.getName(), request);
        }
        return ApiResponse.ok(HttpStatus.CREATED.value(), "Registered successfully");
    }

    @GetMapping
    public ApiResponse<Page<HotelDTO.Response>> getAllVisibleHotels(Pageable pageable) {
        Page<Hotel> hotelPage = hotelService.findAllVisibleHotels(pageable);
        Page<HotelDTO.Response> responsePage = hotelPage.map(HotelDTO.Response::of);
        return ApiResponse.ok(HttpStatus.OK.value(), responsePage);
    }

    @GetMapping("/nation/{nation}")
    public ApiResponse<Page<HotelDTO.Response>> getHotelsByNation(@PathVariable Nation nation, Pageable pageable) {
        Page<HotelDTO.Response> hotelPage = hotelService.findByNation(nation, pageable);
        return ApiResponse.ok(HttpStatus.OK.value(), hotelPage);
    }

    @GetMapping("/name/{name}")
    public ApiResponse<Page<HotelDTO.Response>> searchHotelsByName(
      @PathVariable String name,
      Pageable pageable) {
        Page<Hotel> hotels = hotelService.findHotelsByNameAndVisible(name, pageable);
        Page<HotelDTO.Response> responsePage = hotels.map(HotelDTO.Response::of);
        return ApiResponse.ok(HttpStatus.OK.value(), responsePage);
    }

    @GetMapping("/{hotelId}")
    public ApiResponse<HotelDTO.Response> getAllVisibleRoomsByHotelId(@PathVariable Long hotelId) {
        return ApiResponse.ok(HttpStatus.OK.value(), hotelService.findHotelById(hotelId));
    }

    @PatchMapping("/{hotelId}")
    public ApiResponse<Void> updateData(
      Authentication authentication,
      @PathVariable Long hotelId,
      @Validated
      @RequestBody HotelDTO.Request request
    ) {
        hotelService.updateData(authentication.getName(), hotelId, request);
        return ApiResponse.ok(HttpStatus.NO_CONTENT.value());
    }

    @PatchMapping("/{hotelId}/thumbnails/{thumbnailId}")
    public ApiResponse<Void> updateThumbnail(
      Authentication authentication,
      @PathVariable Long hotelId,
      @PathVariable Long thumbnailId,
      @RequestParam(name = "file", required = false) MultipartFile file
    ) {
        if (file != null) {
            hotelService.updateThumbnail(authentication.getName(), hotelId, thumbnailId, file);
        }
//        Optional.ofNullable(file)
//          .ifPresentOrElse(image ->
//            hotelService.updateThumbnail(authentication.getName(), hotelId, thumbnailId, image),
//            () -> {
//              throw new ApiException(ApiErrorCode.NOT_FOUND_ROOM);
//            }
//          );
        return ApiResponse.ok(HttpStatus.NO_CONTENT.value());
    }

    @DeleteMapping("/{hotelId}")
    public ApiResponse<Void> unregister(
      Authentication authentication,
      @PathVariable Long hotelId
    ) {
        hotelService.unregister(authentication.getName(), hotelId);
        return ApiResponse.ok(HttpStatus.NO_CONTENT.value());
    }

}
