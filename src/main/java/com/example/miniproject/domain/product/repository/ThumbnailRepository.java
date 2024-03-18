package com.example.miniproject.domain.product.repository;

import com.example.miniproject.domain.product.entity.Thumbnail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ThumbnailRepository extends JpaRepository<Thumbnail, Long> {
}
