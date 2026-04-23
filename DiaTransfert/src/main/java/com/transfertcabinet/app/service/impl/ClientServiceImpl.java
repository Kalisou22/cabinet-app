package com.transfertcabinet.app.service.impl;

import com.transfertcabinet.app.dto.request.ClientRequest;
import com.transfertcabinet.app.dto.response.ClientResponse;
import com.transfertcabinet.app.entity.Client;
import com.transfertcabinet.app.exception.BusinessException;
import com.transfertcabinet.app.exception.DuplicateResourceException;
import com.transfertcabinet.app.exception.ResourceNotFoundException;
import com.transfertcabinet.app.mapper.ClientMapper;
import com.transfertcabinet.app.repository.ClientRepository;
import com.transfertcabinet.app.service.ClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;

    @Override
    public ClientResponse create(ClientRequest request) {
        log.info("Creating new client with telephone: {}", request.getTelephone());

        clientRepository.findByTelephone(request.getTelephone()).ifPresent(c -> {
            throw new DuplicateResourceException("Telephone already exists: " + request.getTelephone());
        });

        Client client = clientMapper.toEntity(request);
        Client savedClient = clientRepository.save(client);

        log.info("Client created successfully with ID: {}", savedClient.getId());
        return clientMapper.toResponse(savedClient);
    }

    @Override
    public ClientResponse update(Long id, ClientRequest request) {
        log.info("Updating client with ID: {}", id);

        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client", "id", id));

        if (request.getTelephone() != null && !request.getTelephone().equals(client.getTelephone())) {
            clientRepository.findByTelephone(request.getTelephone()).ifPresent(c -> {
                if (!c.getId().equals(id)) {
                    throw new DuplicateResourceException("Telephone already exists: " + request.getTelephone());
                }
            });
        }

        clientMapper.updateEntity(client, request);
        Client updatedClient = clientRepository.save(client);

        log.info("Client updated successfully with ID: {}", updatedClient.getId());
        return clientMapper.toResponse(updatedClient);
    }

    @Override
    @Transactional(readOnly = true)
    public ClientResponse findById(Long id) {
        log.debug("Finding client by ID: {}", id);

        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client", "id", id));

        return clientMapper.toResponse(client);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClientResponse> findAll() {
        log.debug("Finding all clients");

        return clientRepository.findAllActive().stream()
                .map(clientMapper::toResponse)
                .collect(Collectors.toList());
    }

    // IMPROVEMENT: Pagination
    @Override
    @Transactional(readOnly = true)
    public Page<ClientResponse> findAllPaginated(Pageable pageable) {
        log.debug("Finding all paginated clients");
        return clientRepository.findAllActive(pageable)
                .map(clientMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClientResponse> findAllActive() {
        log.debug("Finding all active clients");

        return clientRepository.findByActifTrue().stream()
                .map(clientMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClientResponse> searchByTelephone(String telephone) {
        log.debug("Searching clients by telephone: {}", telephone);

        return clientRepository.findByTelephoneContaining(telephone).stream()
                .map(clientMapper::toResponse)
                .collect(Collectors.toList());
    }

    // IMPROVEMENT: Clients endettés
    @Override
    @Transactional(readOnly = true)
    public List<ClientResponse> findClientsWithDebt() {
        log.debug("Finding clients with debt");
        return clientRepository.findClientsWithDebt().stream()
                .map(clientMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        log.info("Deleting client with ID: {}", id);

        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client", "id", id));

        // FIX: protection métier
        if (client.getTransactions() != null && !client.getTransactions().isEmpty()) {
            boolean hasActiveTransactions = client.getTransactions().stream()
                    .anyMatch(t -> !t.getDeleted());
            if (hasActiveTransactions) {
                throw new BusinessException("Cannot delete client with existing transactions");
            }
        }

        // IMPROVEMENT: soft delete
        client.setDeleted(true);
        clientRepository.save(client);

        log.info("Client soft deleted successfully with ID: {}", id);
    }
}