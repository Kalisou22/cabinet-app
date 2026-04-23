package com.transfertcabinet.app.service;

import com.transfertcabinet.app.dto.request.UserRequest;
import com.transfertcabinet.app.dto.response.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {
    UserResponse create(UserRequest request);
    UserResponse update(Long id, UserRequest request);
    UserResponse findById(Long id);
    List<UserResponse> findAll();
    void delete(Long id);
    UserResponse findByUsername(String username);

    // IMPROVEMENT: Pagination
    Page<UserResponse> findAllPaginated(Pageable pageable);
}