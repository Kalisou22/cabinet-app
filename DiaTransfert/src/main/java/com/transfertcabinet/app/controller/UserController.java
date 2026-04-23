package com.transfertcabinet.app.controller;

import com.transfertcabinet.app.dto.request.UserRequest;
import com.transfertcabinet.app.dto.response.UserResponse;
import com.transfertcabinet.app.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRequest request) {
        log.info("REST request to create User: {}", request.getUsername());
        UserResponse response = userService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id,
                                                   @Valid @RequestBody UserRequest request) {
        log.info("REST request to update User: {}", id);
        UserResponse response = userService.update(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long id) {
        log.info("REST request to get User: {}", id);
        UserResponse response = userService.findById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        log.info("REST request to get all Users");
        List<UserResponse> responses = userService.findAll();
        return ResponseEntity.ok(responses);
    }

    // IMPROVEMENT: Pagination
    @GetMapping("/paginated")
    public ResponseEntity<Page<UserResponse>> getAllUsersPaginated(Pageable pageable) {
        log.info("REST request to get paginated Users");
        Page<UserResponse> responses = userService.findAllPaginated(pageable);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<UserResponse> getUserByUsername(@PathVariable String username) {
        log.info("REST request to get User by username: {}", username);
        UserResponse response = userService.findByUsername(username);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        log.info("REST request to delete User: {}", id);
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}