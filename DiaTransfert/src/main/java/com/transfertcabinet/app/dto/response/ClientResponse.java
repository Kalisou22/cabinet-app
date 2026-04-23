package com.transfertcabinet.app.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientResponse {
    private Long id;
    private String nom;
    private String telephone;
    private String adresse;
    private Boolean actif;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}