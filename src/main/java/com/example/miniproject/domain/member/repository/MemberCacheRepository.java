package com.example.miniproject.domain.member.repository;

import com.example.miniproject.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Repository
public class MemberCacheRepository {

    private final RedisTemplate<String, Member> memberRedisTemplate;
    private final static Duration MEMBER_CACHE_TTL = Duration.ofDays(3);

    public void setMember(Member member) {
        String key = getKey(member.getEmail());
        memberRedisTemplate.opsForValue().set(key, member, MEMBER_CACHE_TTL);
    }

    public Optional<Member> getMember(String email) {
        String key = getKey(email);
        Optional<Member> member = Optional.ofNullable(memberRedisTemplate.opsForValue().get(key));
        return member;
    }

    private String getKey(String email) {
        return "member:" + email;
    }

}
