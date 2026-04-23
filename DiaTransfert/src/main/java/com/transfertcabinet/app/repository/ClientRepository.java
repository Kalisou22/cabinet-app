package com.transfertcabinet.app.repository;

import com.transfertcabinet.app.entity.Client;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    @Query("SELECT c FROM Client c WHERE c.actif = true AND c.deleted = false")
    List<Client> findByActifTrue();

    @Query("SELECT c FROM Client c WHERE c.telephone LIKE %:telephone% AND c.deleted = false")
    List<Client> findByTelephoneContaining(@Param("telephone") String telephone);

    @Query("SELECT c FROM Client c WHERE c.telephone = :telephone AND c.deleted = false")
    Optional<Client> findByTelephone(@Param("telephone") String telephone);

    @Query("SELECT c FROM Client c WHERE c.deleted = false")
    List<Client> findAllActive();

    // IMPROVEMENT: Pagination avec soft delete
    @Query("SELECT c FROM Client c WHERE c.deleted = false")
    Page<Client> findAllActive(Pageable pageable);

    // IMPROVEMENT: Clients avec dettes
    @Query("SELECT DISTINCT c FROM Client c JOIN c.transactions t WHERE t.status = 'EN_COURS' AND t.resteAPayer > 0 AND c.deleted = false AND t.deleted = false")
    List<Client> findClientsWithDebt();
}