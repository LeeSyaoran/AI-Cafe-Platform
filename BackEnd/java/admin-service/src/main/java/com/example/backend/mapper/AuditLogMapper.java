package com.example.backend.mapper;

import com.example.backend.dto.request.CreateAuditLogRequest;
import com.example.backend.entity.AuditLog;
import org.springframework.stereotype.Component;

@Component
public class AuditLogMapper {

    public AuditLog toEntity(CreateAuditLogRequest request) {
        if (request == null) return null;

        return AuditLog.builder()
                .userId(request.getUserId())
                .action(request.getAction())
                .entityType(request.getEntityType())
                .entityId(request.getEntityId())
                .oldData(request.getOldData())
                .newData(request.getNewData())
                .ipAddress(request.getIpAddress())
                .userAgent(request.getUserAgent())
                .build();
    }
}
