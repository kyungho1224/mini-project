package com.example.miniproject.domain.hotel.repository;

import com.example.miniproject.domain.hotel.constant.HotelStatus;
import com.example.miniproject.domain.hotel.constant.Nation;
import com.example.miniproject.domain.hotel.entity.Hotel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HotelRepository extends JpaRepository<Hotel, Long> {

    // TODO : 전체 상품 조회 -Repository / -by ygg
    Page<Hotel> findAllByHotelStatus(Pageable pageable, HotelStatus hotelStatus);

    // TODO : 카테고리 조회 -Repository / -by ygg
    Page<Hotel> findAllByNationAndHotelStatus(Pageable pageable, Nation nation, HotelStatus hotelStatus);

    // TODO : 호텔명 조회 -Repository / -by ygg
    Page<Hotel> findByNameContainingAndHotelStatus(String name, HotelStatus hotelStatus, Pageable pageable);

}
