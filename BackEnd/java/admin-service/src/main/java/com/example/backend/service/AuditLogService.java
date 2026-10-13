package com.example.backend.service;

import com.example.backend.entity.*;
import com.example.backend.repository.*;
import com.example.backend.dto.request.*;
import com.example.backend.dto.response.*;
import com.example.backend.exception.*;


import java.util.List;
import java.util.UUID;

public interface AuditLogService {
    List<AuditLog> getAuditLogs(UUID userId, String entityType, UUID entityId, String action);
    AuditLog getAuditLogById(UUID id);
    AuditLog recordAuditLog(CreateAuditLogRequest request);
}
