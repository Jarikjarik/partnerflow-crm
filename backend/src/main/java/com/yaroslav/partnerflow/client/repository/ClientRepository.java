package com.yaroslav.partnerflow.client.repository;

import com.yaroslav.partnerflow.client.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long> {

    List<Client> findByArchivedFalse();

    List<Client> findByPartner_IdAndArchivedFalse(Long partnerId);

    Optional<Client> findByIdAndArchivedFalse(Long id);

    Optional<Client> findByPartner_IdAndIdAndArchivedFalse(Long partnerId, Long id);

    long countByArchivedFalse();

    long countByPartner_IdAndArchivedFalse(Long partnerId);
}