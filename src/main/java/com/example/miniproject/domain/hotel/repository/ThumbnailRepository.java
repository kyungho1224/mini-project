package com.example.miniproject.domain.hotel.repository;

import com.example.miniproject.domain.hotel.entity.Thumbnail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ThumbnailRepository extends JpaRepository<Thumbnail, Long> {
}
