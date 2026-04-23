package com.transfertcabinet.app.repository;

import com.transfertcabinet.app.entity.Platform;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlatformRepository extends JpaRepository<Platform, Long> {

    Optional<Platform> findByName(String name);

    boolean existsByName(String name);

    @Query("SELECT p FROM Platform p WHERE p.isActive = true ORDER BY p.displayOrder ASC, p.name ASC")
    List<Platform> findAllActive();

    List<Platform> findAllByOrderByDisplayOrderAscNameAsc();
}