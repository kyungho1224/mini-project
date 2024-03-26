package com.example.miniproject.domain.hotel.repository;

import com.example.miniproject.domain.hotel.entity.Favorite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    Optional<Favorite> findByMemberIdAndHotelId(Long memberId, Long hotelId);

    Page<Favorite> findAllByMemberId(Long memberId, Pageable pageable);

}
