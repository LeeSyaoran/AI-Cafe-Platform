package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Entity
@Table(name = "categories", indexes = {
    @Index(name = "idx_categories_slug", columnList = "slug"),
    @Index(name = "idx_categories_parent_id", columnList = "parent_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Category extends BaseEntity {

    @Column(name = "company_id", nullable = false)
    private UUID companyId;

    @Column(nullable = false)
    private String name;

    @Column(unique = true)
    private String slug;

    private String icon;

    private String color;

    @Column(name = "parent_id")
    private UUID parentId;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column
    @Builder.Default
    private Integer sortOrder = 0;

    @Column
    @Builder.Default
    private Boolean isActive = true;
}
