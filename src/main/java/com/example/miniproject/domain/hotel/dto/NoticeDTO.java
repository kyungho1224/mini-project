package com.example.miniproject.domain.hotel.dto;

import com.example.miniproject.domain.hotel.entity.Notice;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class NoticeDTO {

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class Request {

        @NotBlank(message = "제목은 필수 입력입니다")
        private String title;

        @NotBlank(message = "내용은 필수 입력입니다")
        private String message;

    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Getter
    public static class Response {

        private Long id;

        private String writer;

        private String title;

        private String message;

        private LocalDateTime createdAt;

        private LocalDateTime updatedAt;

        public static Response of(Notice notice) {
            return Response.builder()
              .id(notice.getId())
              .writer(notice.getMember().getName())
              .title(notice.getTitle())
              .message(notice.getMessage())
              .createdAt(notice.getCreatedAt())
              .updatedAt(notice.getUpdatedAt())
              .build();
        }

        public static List<Response> of(List<Notice> notices) {
            return notices.stream().map(Response::of).toList();
        }

    }

}
