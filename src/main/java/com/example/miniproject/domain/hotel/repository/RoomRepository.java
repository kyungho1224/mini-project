package com.example.miniproject.domain.hotel.repository;

import com.example.miniproject.domain.hotel.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room, Long> {
}