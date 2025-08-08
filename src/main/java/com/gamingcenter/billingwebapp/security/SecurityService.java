package com.gamingcenter.billingwebapp.security;

import com.gamingcenter.billingwebapp.model.Bill;
import com.gamingcenter.billingwebapp.model.Order;
import com.gamingcenter.billingwebapp.model.Session;
import com.gamingcenter.billingwebapp.repository.BillRepository;
import com.gamingcenter.billingwebapp.repository.OrderRepository;
import com.gamingcenter.billingwebapp.repository.SessionRepository;
import com.gamingcenter.billingwebapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service("securityService") // This name is used in @PreAuthorize expressions
@RequiredArgsConstructor
public class SecurityService {

    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    private final OrderRepository orderRepository;
    private final BillRepository billRepository;

    /**
     * Checks if the currently authenticated user is either the specified user (by ID)
     * or has the 'ADMIN' role.
     * This is useful for endpoints where a user can access their own data, or an admin
     * can access anyone's data.
     *
     * @param userId The ID of the user whose data is being accessed.
     * @return true if the current user is the specified user or an admin, false otherwise.
     */
    public boolean isUserOrAdmin(Long userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        // Check if the authenticated user is the requested user or an ADMIN
        return userDetails.getId().equals(userId) || authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    /**
     * Checks if the currently authenticated user is either an ADMIN
     * or the user associated with the given order ID.
     *
     * @param orderId The ID of the order being accessed.
     * @return true if the current user is an admin or the order's owner, false otherwise.
     */
    public boolean isUserOrAdminBasedOnOrder(Long orderId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetailsImpl)) {
            return false;
        }

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        // If the authenticated user is an ADMIN, they can access anything
        if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return true;
        }

        // Retrieve the order and check if its user ID matches the authenticated user's ID
        Optional<Order> orderOptional = orderRepository.findById(orderId);
        if (orderOptional.isPresent()) {
            Order order = orderOptional.get();
            // An order might not have a user if it's a guest order, or tied to a session with no explicit user.
            return order.getUser() != null && order.getUser().getId().equals(userDetails.getId());
        }
        return false; // Order not found or no associated user
    }

    /**
     * Checks if the currently authenticated user is either an ADMIN
     * or the user associated with the given bill ID.
     *
     * @param billId The ID of the bill being accessed.
     * @return true if the current user is an admin or the bill's owner, false otherwise.
     */
    public boolean isUserOrAdminBasedOnBill(Long billId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetailsImpl)) {
            return false;
        }

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        // If the authenticated user is an ADMIN, they can access anything
        if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return true;
        }

        // Retrieve the bill and check if its user ID matches the authenticated user's ID
        Optional<Bill> billOptional = billRepository.findById(billId);
        if (billOptional.isPresent()) {
            Bill bill = billOptional.get();
            // A bill might not have a user if it's tied to a session with no explicit user.
            return bill.getUser() != null && bill.getUser().getId().equals(userDetails.getId());
        }
        return false; // Bill not found or no associated user
    }
}