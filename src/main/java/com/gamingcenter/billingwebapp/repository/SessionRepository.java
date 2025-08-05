package com.gamingcenter.billingwebapp.repository;

import com.gamingcenter.billingwebapp.model.Session;
import com.gamingcenter.billingwebapp.model.SessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {
    List<Session> findByUserIdAndStatus(Long userId, SessionStatus status);
    //List<Session> findBySystemIdAndStatus(Long systemId, SessionStatus status);
    List<Session> findByStatus(SessionStatus status);
    Optional<Session> findBySystemIdAndStatus(Long systemId, SessionStatus status); // For finding active session on a system
}
