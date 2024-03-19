package com.example.miniproject.domain.hotel.repository;

import com.example.miniproject.domain.hotel.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
}
