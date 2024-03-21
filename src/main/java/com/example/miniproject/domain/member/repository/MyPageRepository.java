package com.example.miniproject.domain.member.repository;

import com.example.miniproject.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MyPageRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);

}
