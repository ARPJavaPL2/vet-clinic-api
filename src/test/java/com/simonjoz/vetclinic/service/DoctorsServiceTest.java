package com.simonjoz.vetclinic.service;

import com.simonjoz.vetclinic.domain.Doctor;
import com.simonjoz.vetclinic.dto.DoctorDTO;
import com.simonjoz.vetclinic.dto.PageDTO;
import com.simonjoz.vetclinic.exceptions.ResourceNotFoundException;
import com.simonjoz.vetclinic.mappers.PagesMapper;
import com.simonjoz.vetclinic.repository.DoctorsRepo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;

@SpringBootTest
class DoctorsServiceTest {

    private static final long DOCTOR_ONE_ID = 1L;
    private static final long NONE_EXISTING_ID = 100L;
    private static final PageRequest PAGE_REQUEST = PageRequest.of(0, 2);

    private final List<DoctorDTO> doctorsList = List.of(
            new DoctorDTO(DOCTOR_ONE_ID, "DR", "DOCTOR1", "SURNAME1"),
            new DoctorDTO(2L, "DR", "DOCTOR2", "SURNAME2"));

    @Autowired
    private DoctorsService doctorsService;

    @MockBean
    private PagesMapper<DoctorDTO> pagesMapper;

    @MockBean
    private DoctorsRepo doctorsRepo;

    @MockBean
    private AppointmentsService appointmentsService;

    @AfterEach
    void reset() {
        Mockito.reset(pagesMapper, doctorsRepo, appointmentsService);
    }

    @Test
    void tesGetDoctorNoneExistingId() {
        Mockito.doReturn(Optional.empty()).when(doctorsRepo).findById(NONE_EXISTING_ID);

        RuntimeException ex = assertThrows(ResourceNotFoundException.class,
                () -> doctorsService.getDoctor(NONE_EXISTING_ID));

        String expectedMsg = String.format("Doctor with id '%d' not found.", NONE_EXISTING_ID);
        assertEquals(expectedMsg, ex.getMessage());

        Mockito.verify(doctorsRepo).findById(NONE_EXISTING_ID);
    }

    @Test
    void tesGetDoctorSuccess() {
        Doctor expectedDoctor = Doctor.builder().id(DOCTOR_ONE_ID).title("DR")
                .name("DOCTOR1").surname("SURNAME1")
                .appointments(Collections.emptyList()).build();

        Mockito.doReturn(Optional.of(expectedDoctor)).when(doctorsRepo).findById(anyLong());

        Doctor actualDoctor = doctorsService.getDoctor(DOCTOR_ONE_ID);
        assertEquals(expectedDoctor, actualDoctor);

        Mockito.verify(doctorsRepo).findById(DOCTOR_ONE_ID);
    }


    @Test
    void testGetPageSuccess() {
        Page<DoctorDTO> doctorsPage = new PageImpl<>(doctorsList, PAGE_REQUEST, 2L);

        final PageDTO<DoctorDTO> expectedPage =
                new PageDTO<>(1, 2, true, false, false, doctorsList);

        Mockito.doReturn(doctorsPage).when(doctorsRepo).getDoctorsPage(any(PageRequest.class));
        Mockito.doReturn(expectedPage).when(pagesMapper).map(doctorsPage);

        PageDTO<DoctorDTO> actualPage = doctorsService.getPage(PAGE_REQUEST);

        assertFalse(actualPage.isEmpty());
        assertFalse(actualPage.isLast());
        assertTrue(actualPage.isFirst());
        assertEquals(expectedPage, actualPage);
        assertEquals(2, actualPage.getTotalElements());
        assertEquals(1, actualPage.getTotalPages());

        Mockito.verify(doctorsRepo).getDoctorsPage(any(PageRequest.class));
        Mockito.verify(pagesMapper).map(doctorsPage);
    }


    @Test
    void testGetAppointmentsPageByIdNoneExistingId() {
        Mockito.doReturn(false).when(doctorsRepo).existsById(NONE_EXISTING_ID);

        RuntimeException ex = assertThrows(ResourceNotFoundException.class,
                () -> doctorsService.getAppointmentsPageById(PAGE_REQUEST, NONE_EXISTING_ID, null));

        String expectedMsg = String.format("Doctor with id '%d' not found.", NONE_EXISTING_ID);
        assertEquals(expectedMsg, ex.getMessage());

        Mockito.verify(doctorsRepo).existsById(NONE_EXISTING_ID);
    }

    @Test
    void testGetAppointmentsPageByIdDateNull() {
        // NOTE: Mockito.doNothing() is default behavior.

        Mockito.doReturn(true).when(doctorsRepo).existsById(anyLong());
        doctorsService.getAppointmentsPageById(PAGE_REQUEST, DOCTOR_ONE_ID, null);

        Mockito.verify(appointmentsService).getAppointmentsPageByDoctorId(any(PageRequest.class), anyLong());
        Mockito.verify(appointmentsService, never())
                .getAppointmentsPageByDoctorIdForDate(any(PageRequest.class), anyLong(), any(LocalDate.class));
    }

    @Test
    void testGetAppointmentsPageByIdDateIsPresent() {
        Mockito.doReturn(true).when(doctorsRepo).existsById(anyLong());
        doctorsService.getAppointmentsPageById(PAGE_REQUEST, DOCTOR_ONE_ID, LocalDate.now());

        Mockito.verify(appointmentsService)
                .getAppointmentsPageByDoctorIdForDate(any(PageRequest.class), anyLong(), any(LocalDate.class));
        Mockito.verify(appointmentsService, never()).getAppointmentsPageByDoctorId(any(PageRequest.class), anyLong());
    }

}
