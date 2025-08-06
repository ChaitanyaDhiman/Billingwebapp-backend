package com.gamingcenter.billingwebapp.service;

import com.gamingcenter.billingwebapp.dto.GamingSystemDTO;
import com.gamingcenter.billingwebapp.exceptions.ResourceNotFoundException;
import com.gamingcenter.billingwebapp.model.GamingSystem;
import com.gamingcenter.billingwebapp.model.SystemStatus;
import com.gamingcenter.billingwebapp.repository.GamingSystemRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GamingSystemService {

    private final GamingSystemRepository systemRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public GamingSystemDTO createSystem(GamingSystemDTO systemDTO) {
        GamingSystem system = modelMapper.map(systemDTO, GamingSystem.class);
        system.setStatus(SystemStatus.AVAILABLE); //Ensure new system are Available
        GamingSystem savedSystem = systemRepository.save(system);
        return modelMapper.map(savedSystem, GamingSystemDTO.class);
    }

    public GamingSystemDTO getSystemById(Long id) {
        GamingSystem gamingSystem = systemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Gaming System Not Found with id: " + id));
        return modelMapper.map(gamingSystem, GamingSystemDTO.class);
    }

    public List<GamingSystemDTO> getAllSystems() {
        return systemRepository.findAll().stream()
                .map(system -> modelMapper.map(system, GamingSystemDTO.class))
                .collect(Collectors.toList());
    }

    public List<GamingSystemDTO> getAvailableSystems() {
        return systemRepository.findByStatus(SystemStatus.AVAILABLE).stream()
                .map(system -> modelMapper.map(system, GamingSystemDTO.class))
                .collect(Collectors.toList());
    }

    @Transactional
    public GamingSystemDTO updateSystemStatus(Long id, SystemStatus status) {
        GamingSystem system = systemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Gaming System Not Found with id: " + id));
        system.setStatus(status);
        GamingSystem updatedSystem = systemRepository.save(system);
        return modelMapper.map(updatedSystem, GamingSystemDTO.class);
    }

    @Transactional
    public void deleteSystem(Long id) {
        if(!systemRepository.existsById(id)){
            throw new ResourceNotFoundException("Gaming System Not Found with id: " + id);
        }
        systemRepository.deleteById(id);
    }
}
