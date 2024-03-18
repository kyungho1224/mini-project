package com.example.miniproject.domain.product.entity;

import com.example.miniproject.common.entity.BaseEntity;
import com.example.miniproject.domain.product.constant.BedType;
import com.example.miniproject.domain.product.constant.ProductStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Entity
@Table(name = "products", indexes = {
  @Index(name = "name_idx", columnList = "name"),
  @Index(name = "condition_idx", columnList = "condition"),
  @Index(name = "max_capacity_idx", columnList = "max_capacity")
})
public class Product extends BaseEntity {

    @Column(nullable = false, columnDefinition = "VARCHAR(20) NOT NULL COMMENT '카테고리'")
    private String category;

    @Column(nullable = false, columnDefinition = "VARCHAR(20) NOT NULL COMMENT '상품명'")
    private String name;

    @Column(nullable = false, columnDefinition = "DECIMAL(11,4) NOT NULL COMMENT '원가'")
    private BigDecimal cost;

    @Column(nullable = false, columnDefinition = "DECIMAL(11,4) NOT NULL COMMENT '할인율'")
    private BigDecimal discountRate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(20) NOT NULL COMMENT '판매 상태'")
    private ProductStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(20) NOT NULL COMMENT '침대 형태'")
    private BedType bedType;

    @Column(nullable = false, columnDefinition = "VARCHAR(255) NOT NULL COMMENT '부가 옵션'")
    private String condition;

    @Column(nullable = false, columnDefinition = "int NOT NULL COMMENT '최대 인원 수'")
    private int max_capacity;

    @Column(nullable = false, columnDefinition = "VARCHAR(255) NOT NULL COMMENT '해시태그'")
    private String hashtag;

    @Column(nullable = false, columnDefinition = "int NOT NULL COMMENT '수량'")
    private int quantity;

    @Column(nullable = false, columnDefinition = "BIGINT DEFAULT 0 COMMENT '위도'")
    private Long latitude;

    @Column(nullable = false, columnDefinition = "BIGINT DEFAULT 0 COMMENT '경도'")
    private Long longitude;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Thumbnail> thumbnails;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Favorite> favorites;

    public static Product saveAs(String category, String name, BigDecimal cost, BigDecimal discountRate, BedType bedType, String condition, int max_capacity, String hashtag, int quantity) {
        return Product.builder()
          .category(category)
          .name(name)
          .cost(cost)
          .discountRate(discountRate)
          .bedType(bedType)
          .condition(condition)
          .max_capacity(max_capacity)
          .hashtag(hashtag)
          .quantity(quantity)
          .build();
    }

    public void updateStatus(ProductStatus status) {
        this.status = status;
    }

}
