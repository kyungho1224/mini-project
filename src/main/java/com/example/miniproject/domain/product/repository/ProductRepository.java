package com.example.miniproject.domain.product.repository;

import com.example.miniproject.domain.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
