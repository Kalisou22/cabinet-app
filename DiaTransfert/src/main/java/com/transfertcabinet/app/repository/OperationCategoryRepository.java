package com.transfertcabinet.app.repository;

import com.transfertcabinet.app.entity.OperationCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OperationCategoryRepository extends JpaRepository<OperationCategory, Long> {
    Optional<OperationCategory> findByName(String name);
    boolean existsByName(String name);
}