package com.transfertcabinet.app.service;

import com.transfertcabinet.app.dto.request.ClientRequest;
import com.transfertcabinet.app.dto.response.ClientResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ClientService {
    ClientResponse create(ClientRequest request);
    ClientResponse update(Long id, ClientRequest request);
    ClientResponse findById(Long id);
    List<ClientResponse> findAll();
    List<ClientResponse> findAllActive();
    List<ClientResponse> searchByTelephone(String telephone);
    void delete(Long id);

    // IMPROVEMENT: Pagination
    Page<ClientResponse> findAllPaginated(Pageable pageable);

    // IMPROVEMENT: Clients endettés
    List<ClientResponse> findClientsWithDebt();
}