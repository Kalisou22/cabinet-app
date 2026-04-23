package com.transfertcabinet.app.mapper;

import com.transfertcabinet.app.dto.request.ClientRequest;
import com.transfertcabinet.app.dto.response.ClientResponse;
import com.transfertcabinet.app.entity.Client;
import org.springframework.stereotype.Component;

@Component
public class ClientMapper {

    public Client toEntity(ClientRequest request) {
        if (request == null) {
            return null;
        }

        return Client.builder()
                .nom(request.getNom())
                .telephone(request.getTelephone())
                .adresse(request.getAdresse())
                .actif(request.getActif())
                .build();
    }

    public ClientResponse toResponse(Client client) {
        if (client == null) {
            return null;
        }

        return ClientResponse.builder()
                .id(client.getId())
                .nom(client.getNom())
                .telephone(client.getTelephone())
                .adresse(client.getAdresse())
                .actif(client.getActif())
                .createdAt(client.getCreatedAt())
                .updatedAt(client.getUpdatedAt())
                .build();
    }

    public void updateEntity(Client client, ClientRequest request) {
        if (request.getNom() != null) {
            client.setNom(request.getNom());
        }
        if (request.getTelephone() != null) {
            client.setTelephone(request.getTelephone());
        }
        if (request.getAdresse() != null) {
            client.setAdresse(request.getAdresse());
        }
        if (request.getActif() != null) {
            client.setActif(request.getActif());
        }
    }
}