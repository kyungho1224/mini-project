package com.example.miniproject.domain.hotel.repository;


import com.example.miniproject.domain.hotel.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
}
