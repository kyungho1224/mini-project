package com.example.miniproject.domain.member.dto;

import com.example.miniproject.domain.member.constant.MemberRole;
import com.example.miniproject.domain.member.constant.MemberStatus;
import com.example.miniproject.domain.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class MemberDetails implements UserDetails {

    private Long id;

    private String email;

    private String password;

    private String name;

    private MemberRole role;

    private MemberStatus status;

    public static MemberDetails of(Member member) {
        return MemberDetails.builder()
          .id(member.getId())
          .email(member.getEmail())
          .password(member.getPassword())
          .name(member.getName())
          .role(member.getRole())
          .status(member.getStatus())
          .build();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(this.getRole().toString()));
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.status == MemberStatus.CERTIFICATED;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.status == MemberStatus.CERTIFICATED;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.status == MemberStatus.CERTIFICATED;
    }

    @Override
    public boolean isEnabled() {
        return this.status == MemberStatus.CERTIFICATED;
    }

}
