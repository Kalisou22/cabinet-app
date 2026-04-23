package com.transfertcabinet.app.mapper;

import com.transfertcabinet.app.dto.request.OperationCategoryRequest;
import com.transfertcabinet.app.dto.response.OperationCategoryResponse;
import com.transfertcabinet.app.entity.OperationCategory;
import org.springframework.stereotype.Component;

@Component
public class OperationCategoryMapper {

    public OperationCategory toEntity(OperationCategoryRequest request) {
        if (request == null) {
            return null;
        }

        return OperationCategory.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();
    }

    public OperationCategoryResponse toResponse(OperationCategory category) {
        if (category == null) {
            return null;
        }

        return OperationCategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
    }

    public void updateEntity(OperationCategory category, OperationCategoryRequest request) {
        if (request.getName() != null) {
            category.setName(request.getName());
        }
        if (request.getDescription() != null) {
            category.setDescription(request.getDescription());
        }
    }
}