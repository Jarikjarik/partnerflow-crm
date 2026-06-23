package com.yaroslav.partnerflow.billing.service;

import com.yaroslav.partnerflow.auth.security.UserPrincipal;
import com.yaroslav.partnerflow.billing.dto.DealWonMessage;
import com.yaroslav.partnerflow.billing.dto.PartnerCommissionResponse;
import com.yaroslav.partnerflow.billing.entity.BillingEvent;
import com.yaroslav.partnerflow.billing.entity.CommissionRule;
import com.yaroslav.partnerflow.billing.entity.PartnerCommission;
import com.yaroslav.partnerflow.billing.repository.BillingEventRepository;
import com.yaroslav.partnerflow.billing.repository.CommissionRuleRepository;
import com.yaroslav.partnerflow.billing.repository.PartnerCommissionRepository;
import com.yaroslav.partnerflow.common.exception.ResourceNotFoundException;
import com.yaroslav.partnerflow.deal.entity.Deal;
import com.yaroslav.partnerflow.deal.repository.DealRepository;
import com.yaroslav.partnerflow.partner.entity.PartnerProfile;
import com.yaroslav.partnerflow.partner.repository.PartnerProfileRepository;
import com.yaroslav.partnerflow.user.entity.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BillingService {

    private static final BigDecimal DEFAULT_COMMISSION_PERCENT = new BigDecimal("3.00");

    private final DealRepository dealRepository;
    private final PartnerProfileRepository partnerProfileRepository;
    private final CommissionRuleRepository commissionRuleRepository;
    private final PartnerCommissionRepository partnerCommissionRepository;
    private final BillingEventRepository billingEventRepository;

    @Transactional
    public void processDealWon(DealWonMessage message, String rawPayload) {
        Deal deal = dealRepository.findById(message.dealId())
                .orElseThrow(() -> new ResourceNotFoundException("Deal not found with id: " + message.dealId()));

        BillingEvent billingEvent = new BillingEvent();
        billingEvent.setDeal(deal);
        billingEvent.setEventType("DEAL_WON");
        billingEvent.setPayload(rawPayload);
        billingEvent.setProcessed(false);

        billingEventRepository.save(billingEvent);

        if (deal.getPartner() == null) {
            markProcessed(billingEvent);
            return;
        }

        if (partnerCommissionRepository.findByDealId(deal.getId()).isPresent()) {
            markProcessed(billingEvent);
            return;
        }

        BigDecimal dealAmount = deal.getAmount() == null
                ? BigDecimal.ZERO
                : deal.getAmount();

        BigDecimal percent = commissionRuleRepository.findByPartnerIdAndActiveTrue(deal.getPartner().getId())
                .map(CommissionRule::getPercent)
                .orElse(DEFAULT_COMMISSION_PERCENT);

        BigDecimal commissionAmount = dealAmount
                .multiply(percent)
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);

        PartnerCommission commission = new PartnerCommission();
        commission.setPartner(deal.getPartner());
        commission.setDeal(deal);
        commission.setAmount(commissionAmount);
        commission.setPercent(percent);
        commission.setCalculatedAt(Instant.now());

        partnerCommissionRepository.save(commission);

        markProcessed(billingEvent);
    }

    @Transactional(readOnly = true)
    public List<PartnerCommissionResponse> findCommissions(UserPrincipal principal) {
        if (UserRole.PARTNER.name().equals(principal.getRole())) {
            PartnerProfile partner = partnerProfileRepository.findByUserId(principal.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Partner profile not found"));

            return partnerCommissionRepository.findByPartnerId(partner.getId())
                    .stream()
                    .map(this::toResponse)
                    .toList();
        }

        return partnerCommissionRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private void markProcessed(BillingEvent billingEvent) {
        billingEvent.setProcessed(true);
        billingEvent.setProcessedAt(Instant.now());
    }

    private PartnerCommissionResponse toResponse(PartnerCommission commission) {
        return new PartnerCommissionResponse(
                commission.getId(),
                commission.getPartner().getId(),
                commission.getPartner().getUser().getEmail(),
                commission.getDeal().getId(),
                commission.getDeal().getTitle(),
                commission.getAmount(),
                commission.getPercent(),
                commission.getStatus().name(),
                commission.getCalculatedAt(),
                commission.getApprovedAt(),
                commission.getPaidAt()
        );
    }
}