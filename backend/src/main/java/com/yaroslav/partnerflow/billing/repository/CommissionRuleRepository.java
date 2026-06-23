package com.yaroslav.partnerflow.billing.repository;

import com.yaroslav.partnerflow.billing.entity.CommissionRule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommissionRuleRepository extends JpaRepository<CommissionRule, Long> {

    Optional<CommissionRule> findByPartnerIdAndActiveTrue(Long partnerId);
}