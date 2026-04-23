package com.transfertcabinet.app.mapper;

import com.transfertcabinet.app.dto.request.CashOperationRequest;
import com.transfertcabinet.app.dto.response.CashOperationResponse;
import com.transfertcabinet.app.dto.response.CashOperationSummaryResponse;
import com.transfertcabinet.app.entity.CashOperation;
import com.transfertcabinet.app.entity.OperationCategory;
import com.transfertcabinet.app.entity.User;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class CashOperationMapper {

    public CashOperation toEntity(CashOperationRequest request, OperationCategory category, User user) {
        if (request == null) {
            return null;
        }

        return CashOperation.builder()
                .category(category)
                .montant(request.getMontant())
                .type(request.getType())
                .description(request.getDescription())
                .user(user)
                .build();
    }

    public CashOperationResponse toResponse(CashOperation operation) {
        if (operation == null) {
            return null;
        }

        return CashOperationResponse.builder()
                .id(operation.getId())
                .categoryId(operation.getCategory() != null ? operation.getCategory().getId() : null)
                .categoryName(operation.getCategory() != null ? operation.getCategory().getName() : null)
                .montant(operation.getMontant())
                .type(operation.getType())
                .description(operation.getDescription())
                .userId(operation.getUser() != null ? operation.getUser().getId() : null)
                .username(operation.getUser() != null ? operation.getUser().getUsername() : null)
                .createdAt(operation.getCreatedAt())
                .updatedAt(operation.getUpdatedAt())
                .build();
    }

    public void updateEntity(CashOperation operation, CashOperationRequest request,
                             OperationCategory category, User user) {
        if (request.getMontant() != null) {
            operation.setMontant(request.getMontant());
        }
        if (request.getType() != null) {
            operation.setType(request.getType());
        }
        if (request.getDescription() != null) {
            operation.setDescription(request.getDescription());
        }
        if (category != null) {
            operation.setCategory(category);
        }
        if (user != null) {
            operation.setUser(user);
        }
    }

    public CashOperationSummaryResponse toSummaryResponse(BigDecimal totalEntrees, BigDecimal totalSorties, Integer count) {
        return CashOperationSummaryResponse.builder()
                .totalEntrees(totalEntrees)
                .totalSorties(totalSorties)
                .solde(totalEntrees.subtract(totalSorties))
                .operationCount(count)
                .build();
    }
}