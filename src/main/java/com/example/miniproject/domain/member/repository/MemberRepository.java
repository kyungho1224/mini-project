package com.example.miniproject.domain.member.repository;

import com.example.miniproject.domain.member.constant.MemberStatus;
import com.example.miniproject.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    boolean existsByEmail(String email);

    Optional<Member> findByUuid(String uuid);

    Optional<Member> findByEmailAndStatus(String email, MemberStatus status);

    Optional<Member> findByEmail(String email);

}
