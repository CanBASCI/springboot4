package com.org.springboot4.userservice.service;

import com.org.springboot4.userservice.domain.User;
import com.org.springboot4.userservice.dto.CreateUserRequest;
import com.org.springboot4.userservice.dto.UserDto;
import com.org.springboot4.userservice.mapper.UserMapper;
import com.org.springboot4.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    
    @Transactional
    public UserDto createUser(CreateUserRequest request) {
        User user = User.builder()
                .username(request.getUsername())
                .balance(request.getInitialBalance())
                .build();
        
        User saved = userRepository.save(user);
        return userMapper.toDto(saved);
    }
    
    @Transactional(readOnly = true)
    public UserDto getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found: " + id));
        return userMapper.toDto(user);
    }
    
    @Transactional
    public boolean reserveCredit(UUID userId, Long amount) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
        
        if (user.getBalance() >= amount) {
            user.setBalance(user.getBalance() - amount);
            userRepository.save(user);
            return true;
        }
        return false;
    }
}

