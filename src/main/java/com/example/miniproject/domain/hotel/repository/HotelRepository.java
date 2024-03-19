package com.example.miniproject.domain.hotel.repository;

import com.example.miniproject.domain.hotel.entity.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HotelRepository extends JpaRepository<Hotel, Long> {
}
