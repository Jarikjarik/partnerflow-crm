package com.yaroslav.partnerflow.partner.repository;

import com.yaroslav.partnerflow.partner.entity.PartnerProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PartnerProfileRepository extends JpaRepository<PartnerProfile, Long> {

    Optional<PartnerProfile> findByUserId(Long userId);
}