package com.example.miniproject.domain.hotel.service;

import com.example.miniproject.domain.hotel.repository.HotelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class HotelService {

    private final HotelRepository hotelRepository;

}
