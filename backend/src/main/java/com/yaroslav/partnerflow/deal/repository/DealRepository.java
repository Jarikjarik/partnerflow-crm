package com.yaroslav.partnerflow.deal.repository;

import com.yaroslav.partnerflow.deal.entity.Deal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface DealRepository extends JpaRepository<Deal, Long> {

    List<Deal> findByPartner_Id(Long partnerId);

    List<Deal> findByClient_Id(Long clientId);

    Optional<Deal> findByIdAndPartner_Id(Long id, Long partnerId);

    long countByPartner_Id(Long partnerId);

    long countByStatus_CodeIgnoreCase(String statusCode);

    long countByPartner_IdAndStatus_CodeIgnoreCase(Long partnerId, String statusCode);

    @Query("select coalesce(sum(d.amount), 0) from Deal d")
    BigDecimal sumAmount();

    @Query("select coalesce(sum(d.amount), 0) from Deal d where lower(d.status.code) = lower(:statusCode)")
    BigDecimal sumAmountByStatusCode(String statusCode);

    @Query("select coalesce(sum(d.amount), 0) from Deal d where d.partner.id = :partnerId")
    BigDecimal sumAmountByPartnerId(Long partnerId);

    @Query("""
            select coalesce(sum(d.amount), 0)
            from Deal d
            where d.partner.id = :partnerId
              and lower(d.status.code) = lower(:statusCode)
            """)
    BigDecimal sumAmountByPartnerIdAndStatusCode(Long partnerId, String statusCode);
}