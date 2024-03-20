package com.example.miniproject.domain.hotel.repository;

import com.example.miniproject.domain.hotel.constant.Nation;
import com.example.miniproject.domain.hotel.entity.Hotel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HotelRepository extends JpaRepository<Hotel, Long> {

    Page<Hotel> findAllByHotelStatus(Pageable pageable, HotelStatus hotelStatus);

    Page<Hotel> findAllByNationAndHotelStatus(Pageable pageable, Nation nation, HotelStatus hotelStatus);

    Page<Hotel> findByNameContainingAndHotelStatus(String name, HotelStatus hotelStatus, Pageable pageable);

}
