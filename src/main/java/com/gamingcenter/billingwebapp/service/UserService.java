package com.gamingcenter.billingwebapp.service;

import com.gamingcenter.billingwebapp.dto.UserDTO;
import com.gamingcenter.billingwebapp.dto.UserRegisterRequest;
import com.gamingcenter.billingwebapp.exceptions.ResourceNotFoundException;
import com.gamingcenter.billingwebapp.exceptions.UserAlreadyExistsException;
import com.gamingcenter.billingwebapp.model.User;
import com.gamingcenter.billingwebapp.model.UserRole;
import com.gamingcenter.billingwebapp.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    @Transactional
    public UserDTO registerUser(UserRegisterRequest request){
        if(userRepository.findByUsername(request.getUsername()).isPresent()){
            throw new UserAlreadyExistsException("Username already exists: " +request.getUsername());
        }
        User user = modelMapper.map(request, User.class);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        // Set default balance for prepaid users, can be topped up later
        if(user.getRole() == UserRole.PREPAID){
            user.setBalance(BigDecimal.ZERO);
        } else {
            user.setBalance(null);
        }
        User savedUser = userRepository.save(user);
        return modelMapper.map(savedUser, UserDTO.class);
    }

    public UserDTO getUserById(Long id){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return modelMapper.map(user, UserDTO.class);
    }

    public UserDTO getUserByUsername(String username){
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
        return modelMapper.map(user, UserDTO.class);
    }

    public List<UserDTO> getAllUsers(){
        return userRepository.findAll().stream()
                .map(user-> modelMapper.map(user, UserDTO.class))
                .collect(Collectors.toList());
    }

    @Transactional
    public UserDTO topUpPrepaidUserBalance(Long userId, BigDecimal amount){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        if(user.getRole() != UserRole.PREPAID){
            throw new IllegalArgumentException("User is not a prepaid user");
        }
        if(amount.compareTo(BigDecimal.ZERO) <= 0){
            throw new IllegalArgumentException("Top-up amount must be positive");
        }
        user.setBalance(user.getBalance().add(amount));
        User updatedUser = userRepository.save(user);
        return modelMapper.map(updatedUser, UserDTO.class);
    }

    @Transactional
    public void deductBalance(Long userId, BigDecimal amount){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        if(user.getRole() != UserRole.PREPAID){
            throw new IllegalArgumentException("User is not a prepaid user");
        }
        if(amount.compareTo(BigDecimal.ZERO) <= 0){
            throw new IllegalArgumentException("Top-up amount must be positive");
        }
        user.setBalance(user.getBalance().subtract(amount));
        userRepository.save(user);
    }
}
