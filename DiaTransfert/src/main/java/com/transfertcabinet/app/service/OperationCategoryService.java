package com.transfertcabinet.app.service;

import com.transfertcabinet.app.dto.request.OperationCategoryRequest;
import com.transfertcabinet.app.dto.response.OperationCategoryResponse;

import java.util.List;

public interface OperationCategoryService {
    OperationCategoryResponse create(OperationCategoryRequest request);
    OperationCategoryResponse update(Long id, OperationCategoryRequest request);
    OperationCategoryResponse findById(Long id);
    List<OperationCategoryResponse> findAll();
    void delete(Long id);
}