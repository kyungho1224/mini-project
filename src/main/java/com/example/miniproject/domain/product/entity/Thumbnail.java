package com.example.miniproject.domain.product.entity;

import com.example.miniproject.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Entity
@Table(name = "thumbnails")
public class Thumbnail extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "product_id")
    private Product product;

    @Column(nullable = false, columnDefinition = "VARCHAR(255) NOT NULL COMMENT '이미지 경로'")
    private String imgUrl;

}
