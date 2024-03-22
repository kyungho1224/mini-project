package com.example.miniproject.domain.hotel.repository;

import com.example.miniproject.domain.hotel.constant.ActiveStatus;
import com.example.miniproject.domain.hotel.constant.RegisterStatus;
import com.example.miniproject.domain.hotel.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {

    Optional<Room> findByIdAndRegisterStatus(Long roomId, RegisterStatus registerStatus);

    Optional<Room> findByIdAndRegisterStatusAndActiveStatus(Long roomId, RegisterStatus registerStatus, ActiveStatus activeStatus);

}
