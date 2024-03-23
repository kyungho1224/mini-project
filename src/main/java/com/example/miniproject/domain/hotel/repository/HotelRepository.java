package com.example.miniproject.domain.hotel.repository;

import com.example.miniproject.domain.hotel.constant.ActiveStatus;
import com.example.miniproject.domain.hotel.constant.Nation;
import com.example.miniproject.domain.hotel.constant.RegisterStatus;
import com.example.miniproject.domain.hotel.entity.Hotel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HotelRepository extends JpaRepository<Hotel, Long> {

    Page<Hotel> findAllByRegisterStatus(Pageable pageable, RegisterStatus registerStatus);

    Page<Hotel> findAllByNationAndRegisterStatus(Pageable pageable, Nation nation, RegisterStatus registerStatus);

    Page<Hotel> findByNameContainingAndRegisterStatus(String name, RegisterStatus registerStatus, Pageable pageable);

    Optional<Hotel> findByIdAndRegisterStatus(Long id, RegisterStatus registerStatus);

}
