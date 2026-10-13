package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "ai_usage")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class AiUsage extends BaseCreatedEntity {

    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "session_id")
    private UUID sessionId;

    @Column(name = "model_id", length = 50)
    private String modelId;

    @Column(length = 50)
    private String provider;

    @Column(length = 50)
    @Builder.Default
    private String endpoint = "chat";

    @Column(name = "input_tokens")
    @Builder.Default
    private Integer inputTokens = 0;

    @Column(name = "output_tokens")
    @Builder.Default
    private Integer outputTokens = 0;

    @Column(name = "total_tokens")
    @Builder.Default
    private Integer totalTokens = 0;

    @Column(precision = 10, scale = 4)
    @Builder.Default
    private BigDecimal cost = BigDecimal.ZERO;
}
