package com.transfertcabinet.app.service.impl;

import com.transfertcabinet.app.dto.request.OperationCategoryRequest;
import com.transfertcabinet.app.dto.response.OperationCategoryResponse;
import com.transfertcabinet.app.entity.OperationCategory;
import com.transfertcabinet.app.exception.DuplicateResourceException;
import com.transfertcabinet.app.exception.ResourceNotFoundException;
import com.transfertcabinet.app.mapper.OperationCategoryMapper;
import com.transfertcabinet.app.repository.OperationCategoryRepository;
import com.transfertcabinet.app.service.OperationCategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class OperationCategoryServiceImpl implements OperationCategoryService {

    private final OperationCategoryRepository categoryRepository;
    private final OperationCategoryMapper categoryMapper;

    @Override
    public OperationCategoryResponse create(OperationCategoryRequest request) {
        log.info("Creating new operation category: {}", request.getName());

        if (categoryRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("Category already exists: " + request.getName());
        }

        OperationCategory category = categoryMapper.toEntity(request);
        OperationCategory savedCategory = categoryRepository.save(category);

        log.info("Operation category created successfully with ID: {}", savedCategory.getId());
        return categoryMapper.toResponse(savedCategory);
    }

    @Override
    public OperationCategoryResponse update(Long id, OperationCategoryRequest request) {
        log.info("Updating operation category with ID: {}", id);

        OperationCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("OperationCategory", "id", id));

        if (request.getName() != null && !request.getName().equals(category.getName())) {
            if (categoryRepository.existsByName(request.getName())) {
                throw new DuplicateResourceException("Category already exists: " + request.getName());
            }
        }

        categoryMapper.updateEntity(category, request);
        OperationCategory updatedCategory = categoryRepository.save(category);

        log.info("Operation category updated successfully with ID: {}", updatedCategory.getId());
        return categoryMapper.toResponse(updatedCategory);
    }

    @Override
    @Transactional(readOnly = true)
    public OperationCategoryResponse findById(Long id) {
        log.debug("Finding operation category by ID: {}", id);

        OperationCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("OperationCategory", "id", id));

        return categoryMapper.toResponse(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OperationCategoryResponse> findAll() {
        log.debug("Finding all operation categories");

        return categoryRepository.findAll().stream()
                .filter(cat -> !cat.getDeleted())
                .map(categoryMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        log.info("Deleting operation category with ID: {}", id);

        OperationCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("OperationCategory", "id", id));

        // IMPROVEMENT: soft delete
        category.setDeleted(true);
        categoryRepository.save(category);

        log.info("Operation category soft deleted successfully with ID: {}", id);
    }
}