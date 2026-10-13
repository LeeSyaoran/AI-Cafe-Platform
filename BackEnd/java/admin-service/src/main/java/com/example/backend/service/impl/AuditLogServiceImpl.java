package com.example.backend.service.impl;

import com.example.backend.dto.request.CreateAuditLogRequest;
import com.example.backend.entity.AuditLog;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.mapper.AuditLogMapper;
import com.example.backend.repository.AuditLogRepository;
import com.example.backend.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final AuditLogMapper auditLogMapper;

    @Override
    @Transactional(readOnly = true)
    public List<AuditLog> getAuditLogs(UUID userId, String entityType, UUID entityId, String action) {
        if (userId != null) {
            return auditLogRepository.findByUserIdOrderByCreatedAtDesc(userId);
        }
        if (entityType != null && entityId != null) {
            return auditLogRepository.findByEntityTypeAndEntityIdOrderByCreatedAtDesc(entityType, entityId);
        }
        if (action != null && !action.isBlank()) {
            return auditLogRepository.findByActionOrderByCreatedAtDesc(action);
        }
        return auditLogRepository.findAllByOrderByCreatedAtDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public AuditLog getAuditLogById(UUID id) {
        return auditLogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Audit log not found with ID: " + id));
    }

    @Override
    public AuditLog recordAuditLog(CreateAuditLogRequest request) {
        AuditLog log = auditLogMapper.toEntity(request);
        return auditLogRepository.save(log);
    }
}
