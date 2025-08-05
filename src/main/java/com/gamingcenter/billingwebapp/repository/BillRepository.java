package com.gamingcenter.billingwebapp.repository;

import com.gamingcenter.billingwebapp.model.Bill;
import com.gamingcenter.billingwebapp.model.PaymentStatus;
import com.gamingcenter.billingwebapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BillRepository extends JpaRepository<Bill, Long> {
    List<Bill> findByUserId(Long userId);

    Long user(User user);
}
