package com.example.miniproject.domain.member.entity;

import com.example.miniproject.common.entity.BaseEntity;
import com.example.miniproject.domain.member.constant.MemberRole;
import com.example.miniproject.domain.member.constant.MemberStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Entity
@Table(name = "members")
public class Member extends BaseEntity {

    @Column(nullable = false, columnDefinition = "VARCHAR(255) NOT NULL COMMENT '이메일'")
    private String email;

    @Column(nullable = false, columnDefinition = "VARCHAR(255) NOT NULL COMMENT '비밀번호'")
    private String password;

    @Column(nullable = false, columnDefinition = "VARCHAR(255) NOT NULL COMMENT '이름'")
    private String name;

    @Size(min = 8, max = 8)
    @Column(nullable = false, columnDefinition = "VARCHAR(255) NOT NULL COMMENT '생년월일'")
    private String birth;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(255) NOT NULL COMMENT '상태'")
    private MemberStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(255) NOT NULL COMMENT '권한'")
    private MemberRole role;

    @Column(nullable = false, columnDefinition = "DECIMAL(11,4) NOT NULL COMMENT '포인트'")
    private BigDecimal credit;

    @Column(nullable = false, columnDefinition = "VARCHAR(255) NOT NULL COMMENT '인증코드'")
    private String uuid;

    @Column(columnDefinition = "VARCHAR(255) DEFAULT NULL COMMENT '리프레시 토큰'")
    private String refreshToken;

    @Column(columnDefinition = "VARCHAR(255) DEFAULT NULL COMMENT '프로필 이미지'")
    private String profileImage;

    @Column(columnDefinition = "VARCHAR(255) COMMENT '우편번호'")
    private String zipCode;

    @Column(columnDefinition = "VARCHAR(255) COMMENT '국가'")
    private String nation;

    @Column(columnDefinition = "VARCHAR(255) COMMENT '도시'")
    private String city;

    @Column(columnDefinition = "VARCHAR(255) COMMENT '주소'")
    private String address;

    public static Member saveAs(String email, String password, String name, String birth, String uuid) {
        return Member.builder()
          .email(email)
          .password(password)
          .name(name)
          .birth(birth)
          .status(MemberStatus.NOT_CERTIFICATED)
          .role(MemberRole.USER)
          .credit(BigDecimal.valueOf(1000000))
          .uuid(uuid)
          .build();
    }

    public void updateStatus(MemberStatus status) {
        this.status = status;
    }

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void updateProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public void updateAdditionalInfo(String zipCode, String nation, String city, String address) {
        this.zipCode = zipCode;
        this.nation = nation;
        this.city = city;
        this.address = address;
    }

}
