package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Entity
@Table(name = "products", indexes = {
    @Index(name = "idx_products_company_id", columnList = "company_id"),
    @Index(name = "idx_products_name", columnList = "name")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Product extends BaseEntity {

    @Column(name = "company_id", nullable = false)
    private UUID companyId;

    @Column(name = "category_id", nullable = false)
    private UUID categoryId;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String shortDescription;

    @Column(nullable = false)
    @Builder.Default
    private Double price = 0.0;

    @Column(name = "original_price")
    private Double originalPrice;

    @Column(name = "cost_price")
    private Double costPrice;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "images_json", columnDefinition = "TEXT")
    private String imagesJson;

    @Column
    @Builder.Default
    private String unit = "item";

    @Column
    @Builder.Default
    private Integer stock = 0;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "is_featured")
    @Builder.Default
    private Boolean isFeatured = false;

    @Column(name = "is_best_seller")
    @Builder.Default
    private Boolean isBestSeller = false;

    @Column
    @Builder.Default
    private Integer sortOrder = 0;

    @Column(columnDefinition = "TEXT")
    private String optionsJson;

    @Column(columnDefinition = "TEXT")
    private String modifiersJson;

    @Column(columnDefinition = "TEXT")
    private String tagsJson;

    @Column(name = "preparation_time")
    @Builder.Default
    private Integer preparationTime = 15;

    @Column
    @Builder.Default
    private String status = "active";
}
