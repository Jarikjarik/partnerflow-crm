package com.yaroslav.partnerflow.audit.service;

import com.yaroslav.partnerflow.audit.dto.AuditLogResponse;
import com.yaroslav.partnerflow.audit.entity.AuditLog;
import com.yaroslav.partnerflow.audit.repository.AuditLogRepository;
import com.yaroslav.partnerflow.user.entity.User;
import com.yaroslav.partnerflow.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;

    @Transactional(propagation = Propagation.MANDATORY)
    public void log(
            String entityType,
            Long entityId,
            String action,
            String fieldName,
            String oldValue,
            String newValue,
            Long actorId
    ) {
        AuditLog auditLog = new AuditLog();
        auditLog.setEntityType(entityType);
        auditLog.setEntityId(entityId);
        auditLog.setAction(action);
        auditLog.setFieldName(fieldName);
        auditLog.setOldValue(oldValue);
        auditLog.setNewValue(newValue);

        if (actorId != null) {
            userRepository.findById(actorId).ifPresent(auditLog::setActor);
        }

        auditLogRepository.save(auditLog);
    }

    @Transactional(readOnly = true)
    public List<AuditLogResponse> findByEntity(String entityType, Long entityId) {
        return auditLogRepository.findByEntityTypeAndEntityIdOrderByCreatedAtDesc(entityType, entityId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private AuditLogResponse toResponse(AuditLog auditLog) {
        User actor = auditLog.getActor();

        return new AuditLogResponse(
                auditLog.getId(),
                auditLog.getEntityType(),
                auditLog.getEntityId(),
                auditLog.getAction(),
                auditLog.getFieldName(),
                auditLog.getOldValue(),
                auditLog.getNewValue(),
                actor == null ? null : actor.getId(),
                actor == null ? null : actor.getEmail(),
                auditLog.getCreatedAt()
        );
    }
}