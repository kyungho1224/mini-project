package com.example.miniproject.domain.hotel.controller;

import com.example.miniproject.domain.hotel.service.HotelService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/hotels")
public class HotelController {

    private final HotelService hotelService;

    @GetMapping("/welcome")
    public String welcome() {
        return "Welcome~";
    }

}
