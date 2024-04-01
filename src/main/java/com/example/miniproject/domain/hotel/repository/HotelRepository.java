package com.example.miniproject.domain.hotel.repository;

import com.example.miniproject.domain.hotel.constant.*;
import com.example.miniproject.domain.hotel.entity.Hotel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface HotelRepository extends JpaRepository<Hotel, Long> {

    Page<Hotel> findAllByRegisterStatus(Pageable pageable, RegisterStatus registerStatus);

    Page<Hotel> findAllByNationAndRegisterStatus(Pageable pageable, Nation nation, RegisterStatus registerStatus);

    Page<Hotel> findByNameContainingAndRegisterStatus(String name, RegisterStatus registerStatus, Pageable pageable);

    @Query("SELECT h FROM Hotel h WHERE h.name LIKE %:name% AND h.nation = :nation AND h.registerStatus = :registerStatus")
    Page<Hotel> findByNameAndNationContainingAndRegisterStatus(@Param("name") String name, @Param("nation") Nation nation, @Param("registerStatus") RegisterStatus registerStatus, Pageable pageable);

    @Query("SELECT DISTINCT h FROM Hotel h JOIN h.rooms r WHERE h.nation = :nation AND r.type = :roomType AND r.viewType = :viewType AND r.registerStatus = :registerStatus")
    Page<Hotel> findByNationAndRoomTypeAndViewTypeAndRegisterStatus(@Param("nation") Nation nation, @Param("roomType") RoomType roomType, @Param("viewType") ViewType viewType, @Param("registerStatus") RegisterStatus registerStatus, Pageable pageable);

    Optional<Hotel> findByIdAndRegisterStatus(Long id, RegisterStatus registerStatus);

}
