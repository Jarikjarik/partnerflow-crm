package com.yaroslav.partnerflow.deal.repository;

import com.yaroslav.partnerflow.deal.entity.Deal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DealRepository extends JpaRepository<Deal, Long> {

    List<Deal> findByPartner_Id(Long partnerId);

    List<Deal> findByClient_Id(Long clientId);

    Optional<Deal> findByIdAndPartner_Id(Long id, Long partnerId);
}