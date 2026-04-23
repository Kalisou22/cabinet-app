package com.transfertcabinet.app.service.impl;

import com.transfertcabinet.app.dto.request.UserRequest;
import com.transfertcabinet.app.dto.response.UserResponse;
import com.transfertcabinet.app.entity.User;
import com.transfertcabinet.app.exception.DuplicateResourceException;
import com.transfertcabinet.app.exception.ResourceNotFoundException;
import com.transfertcabinet.app.mapper.UserMapper;
import com.transfertcabinet.app.repository.UserRepository;
import com.transfertcabinet.app.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    // FIX: password sécurisé
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponse create(UserRequest request) {
        log.info("Creating new user with username: {}", request.getUsername());

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("Username already exists: " + request.getUsername());
        }

        User user = userMapper.toEntity(request);

        // FIX: password sécurisé
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        User savedUser = userRepository.save(user);

        log.info("User created successfully with ID: {}", savedUser.getId());
        return userMapper.toResponse(savedUser);
    }

    @Override
    public UserResponse update(Long id, UserRequest request) {
        log.info("Updating user with ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        if (request.getUsername() != null && !request.getUsername().equals(user.getUsername())) {
            if (userRepository.existsByUsername(request.getUsername())) {
                throw new DuplicateResourceException("Username already exists: " + request.getUsername());
            }
        }

        userMapper.updateEntity(user, request);

        // FIX: password sécurisé si changé
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        User updatedUser = userRepository.save(user);

        log.info("User updated successfully with ID: {}", updatedUser.getId());
        return userMapper.toResponse(updatedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse findById(Long id) {
        log.debug("Finding user by ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        return userMapper.toResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> findAll() {
        log.debug("Finding all users");

        return userRepository.findAll().stream()
                .filter(u -> !u.getDeleted())
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }

    // IMPROVEMENT: Pagination
    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> findAllPaginated(Pageable pageable) {
        log.debug("Finding all paginated users");
        return userRepository.findAllActive(pageable)
                .map(userMapper::toResponse);
    }

    @Override
    public void delete(Long id) {
        log.info("Deleting user with ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        // IMPROVEMENT: soft delete
        user.setDeleted(true);
        userRepository.save(user);

        log.info("User soft deleted successfully with ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse findByUsername(String username) {
        log.debug("Finding user by username: {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        return userMapper.toResponse(user);
    }
}