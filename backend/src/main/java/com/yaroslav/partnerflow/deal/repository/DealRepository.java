package com.yaroslav.partnerflow.deal.repository;

import com.yaroslav.partnerflow.deal.entity.Deal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DealRepository extends JpaRepository<Deal, Long> {

    List<Deal> findByPartnerId(Long partnerId);

    List<Deal> findByClientId(Long clientId);
}