package com.example.miniproject.domain.hotel.controller;

import com.example.miniproject.common.dto.ApiResponse;
import com.example.miniproject.domain.hotel.constant.Nation;
import com.example.miniproject.domain.hotel.constant.RoomType;
import com.example.miniproject.domain.hotel.constant.SearchType;
import com.example.miniproject.domain.hotel.constant.ViewType;
import com.example.miniproject.domain.hotel.dto.HotelDTO;
import com.example.miniproject.domain.hotel.dto.SearchRequest;
import com.example.miniproject.domain.hotel.dto.ThumbnailDTO;
import com.example.miniproject.domain.hotel.entity.Hotel;
import com.example.miniproject.domain.hotel.service.HotelService;
import com.example.miniproject.exception.ApiErrorCode;
import com.example.miniproject.exception.ApiException;
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

import java.util.Optional;

import static org.springframework.http.HttpStatus.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/hotels")
public class HotelController {

    private final HotelService hotelService;
    private final ObjectMapper objectMapper;

    @GetMapping("/welcome")
    public String welcome() {
        return "안녕 도커~";
    }

    @PostMapping
    public ResponseEntity<ApiResponse<HotelDTO.Response>> register(
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

        HotelDTO.Response response;
        if (files != null && files.length > 0) {
            response = hotelService.create(authentication.getName(), request, files);
        } else {
            response = hotelService.create(authentication.getName(), request);
        }
        return ResponseEntity.status(CREATED).body(ApiResponse.ok(response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<HotelDTO.Response>>> getAllVisibleHotels(
      Authentication authentication,
      Pageable pageable
    ) {
        SearchRequest request;
        if (authentication != null && authentication.isAuthenticated()) {
            request = new SearchRequest(SearchType.ALL_AUTHENTICATION);
            request.setEmail(authentication.getName());
        } else {
            request = new SearchRequest(SearchType.ALL_ANONYMOUS);
        }
        request.setPageable(pageable);
        return ResponseEntity.status(OK).body(ApiResponse.ok(hotelService.searchCollection(request)));
    }

    @GetMapping("/nation/{nation}")
    public ResponseEntity<ApiResponse<Page<HotelDTO.Response>>> getHotelsByNation(
      Authentication authentication,
      @PathVariable Nation nation, Pageable pageable
    ) {
        SearchRequest request;
        if (authentication != null && authentication.isAuthenticated()) {
            request = new SearchRequest(SearchType.NATION_AUTHENTICATION);
            request.setEmail(authentication.getName());
        } else {
            request = new SearchRequest(SearchType.NATION_ANONYMOUS);
        }
        request.setNation(nation);
        request.setPageable(pageable);
        return ResponseEntity.status(OK).body(ApiResponse.ok(hotelService.searchCollection(request)));
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<ApiResponse<Page<HotelDTO.Response>>> searchHotelsByName(
      Authentication authentication,
      @PathVariable String name,
      Pageable pageable
    ) {
        SearchRequest request;
        if (authentication != null && authentication.isAuthenticated()) {
            request = new SearchRequest(SearchType.NAME_AUTHENTICATION);
            request.setEmail(authentication.getName());
        } else {
            request = new SearchRequest(SearchType.NAME_ANONYMOUS);
        }
        request.setName(name);
        request.setPageable(pageable);
        return ResponseEntity.status(OK).body(ApiResponse.ok(hotelService.searchCollection(request)));
    }

    @GetMapping("/")
    public ResponseEntity<ApiResponse<Page<HotelDTO.Response>>> searchHotelsByNameAndNation(
      Authentication authentication,
      @RequestParam("name") String name,
      @RequestParam("nation") String nation,
      Pageable pageable
    ) {
        Nation nationStr = Nation.valueOf(nation.toUpperCase()
          .replace("%", "").replace("\\b", ""));

        SearchRequest request;
        if (authentication != null && authentication.isAuthenticated()) {
            request = new SearchRequest(SearchType.NAME_AND_NATION_AUTHENTICATION);
            request.setEmail(authentication.getName());
        } else {
            request = new SearchRequest(SearchType.NAME_AND_NATION_ANONYMOUS);
        }
        request.setName(name);
        request.setNation(nationStr);
        request.setPageable(pageable);
        return ResponseEntity.status(OK).body(ApiResponse.ok(hotelService.searchCollection(request)));
    }

    @GetMapping("/search/")
    public ResponseEntity<ApiResponse<Page<HotelDTO.Response>>> searchHotelsByNationAndType(
      Authentication authentication,
      @RequestParam("nation") String nation,
      @RequestParam("roomType") String roomType,
      @RequestParam("viewType") String viewType,
      Pageable pageable
    ) {
        Nation nationStr = Nation.valueOf(nation.toUpperCase()
          .replace("%", "").replace("\\b", ""));
        RoomType roomTypeStr = RoomType.valueOf(roomType.toUpperCase()
          .replace("%", "").replace("\\b", ""));
        ViewType viewTypeStr = ViewType.valueOf(viewType.toUpperCase()
          .replace("%", "").replace("\\b", ""));

        SearchRequest request;
        if (authentication != null && authentication.isAuthenticated()) {
            request = new SearchRequest(SearchType.SEARCH_AUTHENTICATION);
            request.setEmail(authentication.getName());
        } else {
            request = new SearchRequest(SearchType.SEARCH_ANONYMOUS);
        }
        request.setNation(nationStr);
        request.setRoomType(roomTypeStr);
        request.setViewType(viewTypeStr);
        request.setPageable(pageable);
        return ResponseEntity.status(OK).body(ApiResponse.ok(hotelService.searchCollection(request)));
    }

    @GetMapping("/{hotelId}")
    public ResponseEntity<ApiResponse<HotelDTO.Response>> getAllVisibleRoomsByHotelId(
      Authentication authentication,
      @PathVariable Long hotelId
    ) {
        if (authentication != null && authentication.isAuthenticated()) {
            return ResponseEntity.status(OK)
              .body(ApiResponse.ok(hotelService.findHotelByIdWithFavorite(authentication.getName(), hotelId)));
        } else {
            return ResponseEntity.status(OK).body(ApiResponse.ok(hotelService.findHotelById(hotelId)));
        }
    }

    @PatchMapping("/{hotelId}")
    public ResponseEntity<ApiResponse<HotelDTO.Response>> updateData(
      Authentication authentication,
      @PathVariable Long hotelId,
      @Validated
      @RequestBody HotelDTO.Request request
    ) {
        Hotel hotel = hotelService.updateData(authentication.getName(), hotelId, request);
        return ResponseEntity.status(ACCEPTED).body(ApiResponse.ok(HotelDTO.Response.of(hotel)));
    }

    @PatchMapping("/{hotelId}/thumbnails/{thumbnailId}")
    public ResponseEntity<ApiResponse<ThumbnailDTO.HotelThumbnailsResponse>> updateThumbnail(
      Authentication authentication,
      @PathVariable Long hotelId,
      @PathVariable Long thumbnailId,
      @RequestParam(name = "file", required = false) MultipartFile file
    ) {
        Optional.ofNullable(file).orElseThrow(() -> new ApiException(ApiErrorCode.NOT_FOUND_IMAGE.getDescription()));
        return ResponseEntity.status(ACCEPTED).body(
          ApiResponse.ok(hotelService.updateThumbnail(authentication.getName(), hotelId, thumbnailId, file))
        );
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
