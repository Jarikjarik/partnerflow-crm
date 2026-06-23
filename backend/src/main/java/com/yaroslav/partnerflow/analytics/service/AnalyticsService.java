package com.yaroslav.partnerflow.analytics.service;

import com.yaroslav.partnerflow.analytics.dto.AnalyticsSummaryResponse;
import com.yaroslav.partnerflow.auth.security.UserPrincipal;
import com.yaroslav.partnerflow.billing.repository.PartnerCommissionRepository;
import com.yaroslav.partnerflow.client.repository.ClientRepository;
import com.yaroslav.partnerflow.common.exception.ResourceNotFoundException;
import com.yaroslav.partnerflow.deal.repository.DealRepository;
import com.yaroslav.partnerflow.partner.entity.PartnerProfile;
import com.yaroslav.partnerflow.partner.repository.PartnerProfileRepository;
import com.yaroslav.partnerflow.user.entity.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private static final String WON_STATUS = "WON";
    private static final String LOST_STATUS = "LOST";

    private final ClientRepository clientRepository;
    private final DealRepository dealRepository;
    private final PartnerCommissionRepository partnerCommissionRepository;
    private final PartnerProfileRepository partnerProfileRepository;

    @Transactional(readOnly = true)
    public AnalyticsSummaryResponse getSummary(UserPrincipal principal) {
        if (isPartner(principal)) {
            PartnerProfile partner = partnerProfileRepository.findByUserId(principal.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Partner profile not found"));

            Long partnerId = partner.getId();

            return new AnalyticsSummaryResponse(
                    clientRepository.countByPartner_IdAndArchivedFalse(partnerId),
                    dealRepository.countByPartner_Id(partnerId),
                    dealRepository.countByPartner_IdAndStatus_CodeIgnoreCase(partnerId, WON_STATUS),
                    dealRepository.countByPartner_IdAndStatus_CodeIgnoreCase(partnerId, LOST_STATUS),
                    dealRepository.sumAmountByPartnerId(partnerId),
                    dealRepository.sumAmountByPartnerIdAndStatusCode(partnerId, WON_STATUS),
                    partnerCommissionRepository.sumAmountByPartnerId(partnerId)
            );
        }

        return new AnalyticsSummaryResponse(
                clientRepository.countByArchivedFalse(),
                dealRepository.count(),
                dealRepository.countByStatus_CodeIgnoreCase(WON_STATUS),
                dealRepository.countByStatus_CodeIgnoreCase(LOST_STATUS),
                dealRepository.sumAmount(),
                dealRepository.sumAmountByStatusCode(WON_STATUS),
                partnerCommissionRepository.sumAmount()
        );
    }

    private boolean isPartner(UserPrincipal principal) {
        return UserRole.PARTNER.name().equals(principal.getRole());
    }
}