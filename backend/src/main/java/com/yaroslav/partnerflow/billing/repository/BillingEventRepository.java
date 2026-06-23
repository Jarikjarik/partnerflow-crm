package com.yaroslav.partnerflow.billing.repository;

import com.yaroslav.partnerflow.billing.entity.BillingEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BillingEventRepository extends JpaRepository<BillingEvent, Long> {

    List<BillingEvent> findByProcessedFalseOrderByCreatedAtAsc();
}