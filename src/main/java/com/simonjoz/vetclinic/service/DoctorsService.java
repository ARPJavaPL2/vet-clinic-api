package com.simonjoz.vetclinic.service;

import com.simonjoz.vetclinic.domain.Doctor;
import com.simonjoz.vetclinic.dto.AppointmentDTO;
import com.simonjoz.vetclinic.dto.DoctorDTO;
import com.simonjoz.vetclinic.dto.PageDTO;
import com.simonjoz.vetclinic.exceptions.ResourceNotFoundException;
import com.simonjoz.vetclinic.mappers.PagesMapper;
import com.simonjoz.vetclinic.repository.DoctorsRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class DoctorsService {

    private final PagesMapper<DoctorDTO> pagesMapper;
    private final DoctorsRepo doctorsRepo;
    private final AppointmentsService appointmentsService;

    @Cacheable("doctor")
    public Doctor getDoctor(Long doctorId) {
        return doctorsRepo.findById(doctorId).orElseThrow(getDoctorNotFoundException(doctorId));
    }

    @Cacheable("doctorsPage")
    public PageDTO<DoctorDTO> getPage(PageRequest pageRequest) {
        Page<DoctorDTO> doctorsPage = doctorsRepo.getDoctorsPage(pageRequest);
        return pagesMapper.map(doctorsPage);
    }

    @Cacheable("doctorAppointmentsPage")
    public PageDTO<AppointmentDTO> getAppointmentsPageById(PageRequest pageRequest, Long doctorId, LocalDate date) {
        throwExceptionIfNotExist(doctorId);
        if (date == null) {
            return appointmentsService.getAppointmentsPageByDoctorId(pageRequest, doctorId);
        }
        return appointmentsService.getAppointmentsPageByDoctorIdForDate(pageRequest, doctorId, date);
    }

    private void throwExceptionIfNotExist(Long doctorId) {
        if (!doctorsRepo.existsById(doctorId)) {
            throw getDoctorNotFoundException(doctorId).get();
        }
    }

    private Supplier<ResourceNotFoundException> getDoctorNotFoundException(Long doctorId) {
        return () -> new ResourceNotFoundException(String.format("Doctor with id '%d' not found.", doctorId));
    }
}
