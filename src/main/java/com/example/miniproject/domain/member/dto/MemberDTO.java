package com.example.miniproject.domain.member.dto;

import com.example.miniproject.domain.member.constant.MemberRole;
import com.example.miniproject.domain.member.constant.MemberStatus;
import com.example.miniproject.domain.member.entity.Member;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

public class MemberDTO {

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class JoinRequest {

        @NotBlank(message = "이메일은 필수입니다")
        @Email(message = "이메일 형식에 맞지 않습니다")
        private String email;

        @NotBlank(message = "비밀번호는 필수입니다")
        private String password;

        @NotBlank(message = "이름은 필수입니다")
        private String name;

        @NotBlank(message = "생년월일은 필수입니다")
        @Size(min = 8, max = 8, message = "8자리(yyyyMMdd) 숫자를 입력해주세요")
        private String birth;

    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Getter
    public static class JoinResponse {

        private Long id;

        private String email;

        private MemberStatus status;

        private MemberRole role;

        public static JoinResponse of(Member member) {
            return JoinResponse.builder()
              .id(member.getId())
              .email(member.getEmail())
              .status(member.getStatus())
              .role(member.getRole())
              .build();
        }

    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class LoginRequest {

        @NotBlank(message = "이메일은 필수입니다")
        @Email(message = "이메일 형식에 맞지 않습니다")
        private String email;

        @NotBlank(message = "비밀번호는 필수입니다")
        private String password;

    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Getter
    public static class LoginResponse {

        private Long id;

        private String email;

        private String name;

        private String birth;

        private MemberStatus status;

        private MemberRole role;

        private BigDecimal credit;

        private String accessToken;

        public static LoginResponse of(Member member, String accessToken) {
            return LoginResponse.builder()
              .id(member.getId())
              .email(member.getEmail())
              .name(member.getName())
              .birth(member.getBirth())
              .status(member.getStatus())
              .role(member.getRole())
              .credit(member.getCredit())
              .accessToken(accessToken)
              .build();
        }

    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Getter
    public static class MyPageResponse {

        private Long id;

        private String profileImage;

        private String name;

        private String email;

        private String birth;

        private String password;

        private MemberStatus status;

        private MemberRole role;

        private String address;

        private String city;

        private String nation;

        private String zipCode;

        private BigDecimal credit;

        public static MyPageResponse of(Member member) {
            return MyPageResponse.builder()
                    .id(member.getId())
                    .profileImage(member.getProfileImage())
                    .name(member.getName())
                    .email(member.getEmail())
                    .birth(member.getBirth())
                    .password(member.getPassword())
                    .status(member.getStatus())
                    .role(member.getRole())
                    .address(member.getAddress())
                    .city(member.getCity())
                    .nation(member.getNation())
                    .zipCode(member.getZipCode())
                    .credit(member.getCredit())
                    .build();
        }

    }

}
