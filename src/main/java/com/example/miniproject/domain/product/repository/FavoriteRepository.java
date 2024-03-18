package com.example.miniproject.domain.product.repository;

import com.example.miniproject.domain.product.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
}
