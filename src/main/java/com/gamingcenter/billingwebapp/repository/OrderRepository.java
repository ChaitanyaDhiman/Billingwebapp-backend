package com.gamingcenter.billingwebapp.repository;

import com.gamingcenter.billingwebapp.model.Order;
import com.gamingcenter.billingwebapp.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findBySessionId(Long sessionId);
    List<Order> findByUserId(Long userId);
    List<Order> findByStatus(OrderStatus status);
}
