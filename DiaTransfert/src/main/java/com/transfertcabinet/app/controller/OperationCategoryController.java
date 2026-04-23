package com.transfertcabinet.app.controller;

import com.transfertcabinet.app.dto.request.OperationCategoryRequest;
import com.transfertcabinet.app.dto.response.OperationCategoryResponse;
import com.transfertcabinet.app.service.OperationCategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/v1/categories")
@RequiredArgsConstructor
public class OperationCategoryController {

    private final OperationCategoryService categoryService;

    @PostMapping
    public ResponseEntity<OperationCategoryResponse> createCategory(
            @Valid @RequestBody OperationCategoryRequest request) {
        log.info("REST request to create Operation Category: {}", request.getName());
        OperationCategoryResponse response = categoryService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OperationCategoryResponse> updateCategory(
            @PathVariable Long id, @Valid @RequestBody OperationCategoryRequest request) {
        log.info("REST request to update Operation Category: {}", id);
        OperationCategoryResponse response = categoryService.update(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OperationCategoryResponse> getCategory(@PathVariable Long id) {
        log.info("REST request to get Operation Category: {}", id);
        OperationCategoryResponse response = categoryService.findById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<OperationCategoryResponse>> getAllCategories() {
        log.info("REST request to get all Operation Categories");
        List<OperationCategoryResponse> responses = categoryService.findAll();
        return ResponseEntity.ok(responses);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        log.info("REST request to delete Operation Category: {}", id);
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}