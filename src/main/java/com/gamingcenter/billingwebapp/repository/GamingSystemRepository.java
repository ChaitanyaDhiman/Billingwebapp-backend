package com.gamingcenter.billingwebapp.repository;

import com.gamingcenter.billingwebapp.model.GamingSystem;
import com.gamingcenter.billingwebapp.model.SystemStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GamingSystemRepository extends JpaRepository<GamingSystem, Long> {
    List<GamingSystem> findByStatus(SystemStatus status);
    Optional<GamingSystem> findByName(String name);
}
