package com.transfertcabinet.app.service.impl;

import com.transfertcabinet.app.entity.Platform;
import com.transfertcabinet.app.exception.BusinessException;
import com.transfertcabinet.app.exception.ResourceNotFoundException;
import com.transfertcabinet.app.repository.PlatformRepository;
import com.transfertcabinet.app.service.PlatformService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PlatformServiceImpl implements PlatformService {

    private final PlatformRepository platformRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Platform> findAll() {
        log.debug("Finding all platforms");
        return platformRepository.findAllByOrderByDisplayOrderAscNameAsc();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Platform> findAllActive() {
        log.debug("Finding all active platforms");
        return platformRepository.findAllActive();
    }

    @Override
    @Transactional(readOnly = true)
    public Platform findById(Long id) {
        log.debug("Finding platform by ID: {}", id);
        return platformRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Platform", "id", id));
    }

    @Override
    @Transactional(readOnly = true)
    public Platform findByName(String name) {
        log.debug("Finding platform by name: {}", name);
        return platformRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Platform", "name", name));
    }

    @Override
    public Platform create(Platform platform) {
        log.info("Creating new platform: {}", platform.getName());

        if (platformRepository.existsByName(platform.getName())) {
            throw new BusinessException("Une plateforme avec ce nom existe déjà : " + platform.getName());
        }

        Platform saved = platformRepository.save(platform);
        log.info("Platform created successfully with ID: {}", saved.getId());
        return saved;
    }

    @Override
    public Platform update(Long id, Platform platform) {
        log.info("Updating platform with ID: {}", id);

        Platform existing = platformRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Platform", "id", id));

        if (!existing.getName().equals(platform.getName()) &&
                platformRepository.existsByName(platform.getName())) {
            throw new BusinessException("Une plateforme avec ce nom existe déjà : " + platform.getName());
        }

        existing.setName(platform.getName());
        existing.setDisplayOrder(platform.getDisplayOrder());

        Platform updated = platformRepository.save(existing);
        log.info("Platform updated successfully with ID: {}", updated.getId());
        return updated;
    }

    @Override
    public void delete(Long id) {
        log.info("Deleting platform with ID: {}", id);

        if (!platformRepository.existsById(id)) {
            throw new ResourceNotFoundException("Platform", "id", id);
        }

        platformRepository.deleteById(id);
        log.info("Platform deleted successfully with ID: {}", id);
    }

    @Override
    public void toggleStatus(Long id) {
        log.info("Toggling status for platform with ID: {}", id);

        Platform platform = platformRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Platform", "id", id));

        platform.setIsActive(!platform.getIsActive());
        platformRepository.save(platform);

        log.info("Platform status toggled to: {}", platform.getIsActive());
    }
}