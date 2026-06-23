package com.yaroslav.partnerflow.billing.repository;

import com.yaroslav.partnerflow.billing.entity.PartnerCommission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PartnerCommissionRepository extends JpaRepository<PartnerCommission, Long> {

    Optional<PartnerCommission> findByDealId(Long dealId);

    List<PartnerCommission> findByPartnerId(Long partnerId);
}