package com.yaroslav.partnerflow.deal.repository;

import com.yaroslav.partnerflow.deal.entity.DealStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DealStatusRepository extends JpaRepository<DealStatus, Long> {

    Optional<DealStatus> findByCode(String code);

    List<DealStatus> findByActiveTrueOrderBySortOrderAsc();
}