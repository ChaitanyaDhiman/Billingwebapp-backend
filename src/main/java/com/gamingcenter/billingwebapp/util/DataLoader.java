package com.gamingcenter.billingwebapp.util;

import com.gamingcenter.billingwebapp.model.User;
import com.gamingcenter.billingwebapp.model.UserRole;
import com.gamingcenter.billingwebapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {

        //Create user if they dont exist
        if(userRepository.findByUsername("admin").isEmpty()) {
            User adminUser = new User("admin", passwordEncoder.encode("admin"), UserRole.ADMIN, "admin@gmail.com");
            userRepository.save(adminUser);
        }
        if(userRepository.findByUsername("prepaid").isEmpty()) {
            User prepaidUser = new User("prepaid", passwordEncoder.encode("password"), UserRole.PREPAID, "prepaid@gmail.com", BigDecimal.valueOf(100));
            userRepository.save(prepaidUser);
        }
        if(userRepository.findByUsername("postpaid").isEmpty()) {
            User postpaidUser = new User("postpaid", passwordEncoder.encode("password"), UserRole.POSTPAID, "postpaid@gmail.com", BigDecimal.valueOf(100));
            userRepository.save(postpaidUser);
        }
    }
}
