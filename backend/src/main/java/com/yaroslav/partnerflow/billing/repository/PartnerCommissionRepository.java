package com.yaroslav.partnerflow.billing.repository;

import com.yaroslav.partnerflow.billing.entity.PartnerCommission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface PartnerCommissionRepository extends JpaRepository<PartnerCommission, Long> {

    Optional<PartnerCommission> findByDealId(Long dealId);

    List<PartnerCommission> findByPartnerId(Long partnerId);

    @Query("select coalesce(sum(c.amount), 0) from PartnerCommission c")
    BigDecimal sumAmount();

    @Query("select coalesce(sum(c.amount), 0) from PartnerCommission c where c.partner.id = :partnerId")
    BigDecimal sumAmountByPartnerId(Long partnerId);
}