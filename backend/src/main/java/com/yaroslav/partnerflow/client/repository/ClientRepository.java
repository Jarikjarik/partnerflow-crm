package com.yaroslav.partnerflow.client.repository;

import com.yaroslav.partnerflow.client.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClientRepository extends JpaRepository<Client, Long> {

    List<Client> findByPartnerIdAndArchivedFalse(Long partnerId);

    List<Client> findByArchivedFalse();
}