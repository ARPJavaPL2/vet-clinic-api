package com.simonjoz.vetclinic.service;

import com.simonjoz.vetclinic.dto.TimingDetailsDTO;
import com.simonjoz.vetclinic.exceptions.ResourceNotFoundException;
import com.simonjoz.vetclinic.repository.VisitDetailsRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VisitDetailsService {

    private final VisitDetailsRepo visitDetailsRepo;

    @Cacheable("doctorTimeDetails")
    public TimingDetailsDTO getTimingDetails(Long doctorId) {
        return visitDetailsRepo.getTimingDetails(doctorId).orElseThrow(() -> new
                ResourceNotFoundException(String.format("Timing details not found for doctor with id '%d'.", doctorId)));
    }
}
