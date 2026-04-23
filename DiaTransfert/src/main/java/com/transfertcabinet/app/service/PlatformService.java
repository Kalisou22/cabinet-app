package com.transfertcabinet.app.service;

import com.transfertcabinet.app.entity.Platform;

import java.util.List;

public interface PlatformService {
    List<Platform> findAll();
    List<Platform> findAllActive();
    Platform findById(Long id);
    Platform findByName(String name);
    Platform create(Platform platform);
    Platform update(Long id, Platform platform);
    void delete(Long id);
    void toggleStatus(Long id);
}