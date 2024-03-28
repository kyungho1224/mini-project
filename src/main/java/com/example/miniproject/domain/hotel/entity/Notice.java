package com.example.miniproject.domain.hotel.entity;


import com.example.miniproject.common.entity.BaseEntity;
import com.example.miniproject.domain.hotel.constant.RegisterStatus;
import com.example.miniproject.domain.hotel.dto.NoticeDTO;
import com.example.miniproject.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
public class Notice extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "hotel_id")
    private Hotel hotel;

    @Column(nullable = false, columnDefinition = "VARCHAR(255) NOT NULL COMMENT '게시글 제목'")
    private String title;

    @Column(nullable = false, columnDefinition = "VARCHAR(255) NOT NULL COMMENT '게시글 내용'")
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(255) NOT NULL COMMENT '게시글 상태'")
    private RegisterStatus registerStatus;

    public static Notice saveAs(Member member, Hotel hotel, NoticeDTO.Request request) {
        return new Notice(member, hotel, request.getTitle(), request.getMessage(), RegisterStatus.VISIBLE);
    }

    public void update(NoticeDTO.Request request) {
        this.title = request.getTitle();
        this.message = request.getMessage();
    }

    public void delete() {
        this.registerStatus = RegisterStatus.INVISIBLE;
    }

}
