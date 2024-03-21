package com.example.miniproject.domain.hotel.repository;

import com.example.miniproject.domain.hotel.constant.RegisterStatus;
import com.example.miniproject.domain.hotel.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {

    List<Room> findAllByHotelIdAndRegisterStatus(Long hotelId, RegisterStatus registerStatus);

    Optional<Room> findByIdAndHotelIdAndRegisterStatus(Long roomId, Long hotelId, RegisterStatus registerStatus);

    Optional<Room> findByIdAndRegisterStatus(Long roomId, RegisterStatus registerStatus);

}
