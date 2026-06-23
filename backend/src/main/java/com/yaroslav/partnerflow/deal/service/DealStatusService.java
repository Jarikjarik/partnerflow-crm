package com.yaroslav.partnerflow.deal.service;

import com.yaroslav.partnerflow.deal.dto.DealStatusResponse;
import com.yaroslav.partnerflow.deal.entity.DealStatus;
import com.yaroslav.partnerflow.deal.repository.DealStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DealStatusService {

    private final DealStatusRepository dealStatusRepository;

    @Transactional(readOnly = true)
    public List<DealStatusResponse> findAllActive() {
        return dealStatusRepository.findByActiveTrueOrderBySortOrderAsc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private DealStatusResponse toResponse(DealStatus status) {
        return new DealStatusResponse(
                status.getId(),
                status.getCode(),
                status.getName(),
                status.getSortOrder(),
                status.isFinalStatus(),
                status.isActive()
        );
    }
}