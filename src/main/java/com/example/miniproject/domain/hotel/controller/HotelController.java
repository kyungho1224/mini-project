package com.example.miniproject.domain.hotel.controller;

import com.example.miniproject.common.dto.ApiResponse;
import com.example.miniproject.domain.hotel.constant.Nation;
import com.example.miniproject.domain.hotel.dto.HotelDTO;
import com.example.miniproject.domain.hotel.entity.Hotel;
import com.example.miniproject.domain.hotel.service.HotelService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    // TODO : 전체 상품 조회 -Controller / -by ygg
    @GetMapping
    public ApiResponse<Page<HotelDTO.Response>> getAllVisibleHotels(Pageable pageable) {
        Page<Hotel> hotelPage = hotelService.findAllVisibleHotels(pageable);
        Page<HotelDTO.Response> responsePage = hotelPage.map(HotelDTO.Response::of);
        return ApiResponse.ok(HttpStatus.OK.value(), responsePage);
    }

    // TODO : 카테고리 조회 -Controller / -by ygg
    @GetMapping("/nation/{nation}")
    public ApiResponse<Page<HotelDTO.Response>> getHotelsByNation(@PathVariable Nation nation, Pageable pageable) {
        Page<HotelDTO.Response> hotelPage = hotelService.findByNation(nation, pageable);
        return ApiResponse.ok(HttpStatus.OK.value(), hotelPage);
    }

    // TODO : 호텔명 조회 -Controller / -by ygg
    @GetMapping("/name/{name}")
    public ApiResponse<Page<HotelDTO.Response>> searchHotelsByName(
            @PathVariable String name,
            Pageable pageable) {
        Page<Hotel> hotels = hotelService.findHotelsByNameAndVisible(name, pageable);
        Page<HotelDTO.Response> responsePage = hotels.map(HotelDTO.Response::of);
        return ApiResponse.ok(HttpStatus.OK.value(), responsePage);
    }


}
