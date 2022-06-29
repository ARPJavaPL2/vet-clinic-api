package com.simonjoz.vetclinic.service;

import com.simonjoz.vetclinic.domain.*;
import com.simonjoz.vetclinic.dto.AppointmentDTO;
import com.simonjoz.vetclinic.dto.CustomerDTO;
import com.simonjoz.vetclinic.dto.PageDTO;
import com.simonjoz.vetclinic.exceptions.InvalidPinException;
import com.simonjoz.vetclinic.exceptions.ResourceNotFoundException;
import com.simonjoz.vetclinic.exceptions.UnavailableDateException;
import com.simonjoz.vetclinic.mappers.CustomerAppointmentMapper;
import com.simonjoz.vetclinic.mappers.PagesMapper;
import com.simonjoz.vetclinic.repository.CustomersRepo;
import com.simonjoz.vetclinic.repository.DoctorsRepo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

@SpringBootTest
class CustomersServiceTest {

    private static final int VALID_PIN = 1234;
    private static final int INVALID_PIN = 4321;
    private static final long CUSTOMER_ONE_ID = 1L;
    private static final long DOCTOR_ONE_ID = 1L;
    private static final long NONE_EXISTING_ID = 100L;
    private static final PageRequest PAGE_REQUEST = PageRequest.of(0, 2);

    private static final Customer CUSTOMER_ONE =
            new Customer(CUSTOMER_ONE_ID, VALID_PIN, "CUSTOMER1", "SURNAME1", Collections.emptyList());

    private static final AppointmentRequest APPOINTMENT_REQUEST = new AppointmentRequest(VALID_PIN, DOCTOR_ONE_ID,
            "some note here", LocalDate.now(), LocalTime.now().plusMinutes(2));


    private final List<CustomerDTO> customersList = List.of(
            new CustomerDTO(1L, VALID_PIN, "CUSTOMER1", "SURNAME1"),
            new CustomerDTO(2L, VALID_PIN, "CUSTOMER2", "SURNAME2"));

    @Autowired
    private CustomersService customersService;

    @Autowired
    private CustomerAppointmentMapper customerAppointmentMapper;

    @MockBean
    private PagesMapper<CustomerDTO> pagesMapper;

    @MockBean
    private CustomersRepo customersRepo;

    @MockBean
    private AppointmentsService appointmentsService;

    @MockBean
    private DoctorsRepo doctorRepo;


    @AfterEach
    void reset() {
        Mockito.reset(pagesMapper, customersRepo, doctorRepo, appointmentsService);
    }

    @Test
    void testGetCustomerNoneExistingId() {
        Mockito.doReturn(Optional.empty()).when(customersRepo).findById(NONE_EXISTING_ID);

        RuntimeException ex = assertThrows(ResourceNotFoundException.class,
                () -> customersService.getCustomer(NONE_EXISTING_ID));

        String expectedMsg = String.format("Customer with id '%d' not found.", NONE_EXISTING_ID);
        assertEquals(expectedMsg, ex.getMessage());

        Mockito.verify(customersRepo).findById(NONE_EXISTING_ID);
    }

    @Test
    void testGetCustomerSuccess() {
        Mockito.doReturn(Optional.of(CUSTOMER_ONE)).when(customersRepo).findById(anyLong());

        Customer actualCustomer = customersService.getCustomer(CUSTOMER_ONE_ID);
        assertEquals(CUSTOMER_ONE, actualCustomer);

        Mockito.verify(customersRepo).findById(CUSTOMER_ONE_ID);
    }

    @Test
    void testGetPageSuccess() {
        Page<CustomerDTO> customersPage = new PageImpl<>(customersList, PAGE_REQUEST, 2L);

        final PageDTO<CustomerDTO> expectedPage =
                new PageDTO<>(1, 2, true, false, false, customersList);

        Mockito.doReturn(customersPage).when(customersRepo).getCustomersPage(any(PageRequest.class));
        Mockito.doReturn(expectedPage).when(pagesMapper).map(customersPage);

        PageDTO<CustomerDTO> actualPage = customersService.getPage(PAGE_REQUEST);

        assertFalse(actualPage.isEmpty());
        assertFalse(actualPage.isLast());
        assertTrue(actualPage.isFirst());
        assertEquals(expectedPage, actualPage);
        assertEquals(2, actualPage.getTotalElements());
        assertEquals(1, actualPage.getTotalPages());

        Mockito.verify(customersRepo).getCustomersPage(any(PageRequest.class));
        Mockito.verify(pagesMapper).map(customersPage);
    }


    @Test
    void testMakeAppointmentInvalidPin() {
        Mockito.doReturn(Optional.of(CUSTOMER_ONE)).when(customersRepo).findById(anyLong());

        var appointmentRequest = new AppointmentRequest(INVALID_PIN, DOCTOR_ONE_ID,
                "some note here", LocalDate.now(), LocalTime.now().plusMinutes(1));

        RuntimeException ex = assertThrows(InvalidPinException.class,
                () -> customersService.makeAppointment(appointmentRequest, CUSTOMER_ONE_ID));

        String expectedMsg = String.format("Given pin '%d' is invalid", INVALID_PIN);
        assertEquals(expectedMsg, ex.getMessage());

        Mockito.verify(customersRepo).findById(anyLong());
    }

    @Test
    void testMakeAppointmentDateNoneExistingDoctorId() {
        Mockito.doReturn(Optional.empty()).when(customersRepo).findById(anyLong());

        assertThrows(ResourceNotFoundException.class,
                () -> customersService.makeAppointment(APPOINTMENT_REQUEST, CUSTOMER_ONE_ID));

        Mockito.verify(customersRepo).findById(anyLong());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 5, 10, 40, 120})
    void testMakeAppointmentDateInvalidTime(int minutes) {
        var invalidRequest = new AppointmentRequest(VALID_PIN, DOCTOR_ONE_ID,
                "some note here", LocalDate.now(), LocalTime.now().minusMinutes(minutes));

        assertThrows(UnavailableDateException.class,
                () -> customersService.makeAppointment(invalidRequest, CUSTOMER_ONE_ID));

        Mockito.verify(customersRepo, Mockito.never()).findById(anyLong());
        Mockito.verify(appointmentsService, Mockito.never())
                .checkDateAvailabilityForDoctor(any(AppointmentRequest.class));
        Mockito.verify(doctorRepo, Mockito.never()).findById(anyLong());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 5, 10, 40, 120})
    void testMakeAppointmentDateValidTime(int value) {

        VisitDetails visitDetails = VisitDetails.builder()
                .id(1L).doctor(null)
                .openingAt(LocalTime.of(6, 0))
                .closingAt(LocalTime.of(16, 0))
                .visitDurationInMinutes(30)
                .visitPrice(BigDecimal.TEN).build();

        Doctor doctor = new Doctor(DOCTOR_ONE_ID, "DR", "DOCTOR1", "SURNAME1", visitDetails, Collections.emptyList());
        visitDetails.setDoctor(doctor);

        var validRequest = new AppointmentRequest(VALID_PIN, DOCTOR_ONE_ID, "some note here",
                LocalDate.now().plusDays(value), LocalTime.now().minusMinutes(value));

        Mockito.doReturn(Optional.of(CUSTOMER_ONE)).when(customersRepo).findById(anyLong());
        Mockito.doReturn(Optional.of(doctor)).when(doctorRepo).findById(anyLong());

        customersService.makeAppointment(validRequest, CUSTOMER_ONE_ID);
        Mockito.verify(customersRepo).findById(anyLong());
        Mockito.verify(appointmentsService).checkDateAvailabilityForDoctor(any(AppointmentRequest.class));
        Mockito.verify(doctorRepo).findById(anyLong());
        Mockito.verify(appointmentsService).addAppointment(any(Appointment.class));
    }

    @Test
    void testMakeAppointmentSuccess() {
        VisitDetails visitDetails = VisitDetails.builder()
                .id(1L).doctor(null)
                .openingAt(LocalTime.of(6, 0))
                .closingAt(LocalTime.of(16, 0))
                .visitDurationInMinutes(30)
                .visitPrice(BigDecimal.TEN).build();

        Doctor expectedDoctor = new Doctor(DOCTOR_ONE_ID, "DR", "DOCTOR1",
                "SURNAME1", visitDetails, Collections.emptyList());
        visitDetails.setDoctor(expectedDoctor);

        Mockito.doReturn(Optional.of(CUSTOMER_ONE)).when(customersRepo).findById(anyLong());
        Mockito.doReturn(Optional.of(expectedDoctor)).when(doctorRepo).findById(anyLong());

        Appointment appointment = Appointment.builder()
                .customer(CUSTOMER_ONE)
                .doctor(expectedDoctor)
                .note(APPOINTMENT_REQUEST.getNote())
                .scheduledDate(APPOINTMENT_REQUEST.getDate())
                .scheduledTime(APPOINTMENT_REQUEST.getTime())
                .timestamp(LocalDateTime.of(APPOINTMENT_REQUEST.getDate(), APPOINTMENT_REQUEST.getTime()))
                .build();

        AppointmentDTO expectedAppointmentDTO = customerAppointmentMapper.map(appointment);
        Mockito.doReturn(expectedAppointmentDTO).when(appointmentsService).addAppointment(any(Appointment.class));
        AppointmentDTO actualAppointmentDTO = customersService.makeAppointment(APPOINTMENT_REQUEST, CUSTOMER_ONE_ID);

        assertEquals(expectedAppointmentDTO, actualAppointmentDTO);
        assertEquals(expectedAppointmentDTO.getId(), actualAppointmentDTO.getId());
        assertEquals(expectedAppointmentDTO.getNote(), actualAppointmentDTO.getNote());
        assertEquals(expectedAppointmentDTO.getPersonName(), actualAppointmentDTO.getPersonName());
        assertEquals(expectedAppointmentDTO.getPersonSurname(), actualAppointmentDTO.getPersonSurname());
        assertEquals(expectedAppointmentDTO.getScheduledDate(), actualAppointmentDTO.getScheduledDate());
        assertEquals(expectedAppointmentDTO.getScheduledTime(), actualAppointmentDTO.getScheduledTime());

        Mockito.verify(customersRepo).findById(anyLong());
        Mockito.verify(doctorRepo).findById(anyLong());
        Mockito.verify(appointmentsService).addAppointment(any(Appointment.class));
    }


    @Test
    void testCancelAppointmentInvalidPin() {
        Mockito.doReturn(Optional.of(VALID_PIN)).when(customersRepo).getCustomerPinById(anyLong());

        var appointmentRequest = new AppointmentRequest(INVALID_PIN, DOCTOR_ONE_ID,
                "some note here", LocalDate.now(), LocalTime.now().plusMinutes(1));

        RuntimeException ex = assertThrows(InvalidPinException.class,
                () -> customersService.cancelAppointment(appointmentRequest, CUSTOMER_ONE_ID));

        String expectedMsg = String.format("Given pin '%d' is invalid", INVALID_PIN);
        assertEquals(expectedMsg, ex.getMessage());

        Mockito.verify(customersRepo).getCustomerPinById(anyLong());
    }


    @Test
    void testCancelAppointmentNoneExistingId() {
        Mockito.doReturn(Optional.empty()).when(customersRepo).getCustomerPinById(anyLong());

        assertThrows(ResourceNotFoundException.class,
                () -> customersService.cancelAppointment(APPOINTMENT_REQUEST, CUSTOMER_ONE_ID));

        Mockito.verify(customersRepo).getCustomerPinById(anyLong());
    }

    @Test
    void testCancelAppointmentSuccess() {
        Mockito.doReturn(Optional.of(VALID_PIN)).when(customersRepo).getCustomerPinById(anyLong());
        customersService.cancelAppointment(APPOINTMENT_REQUEST, CUSTOMER_ONE_ID);

        LocalDateTime appointmentTimestamp = LocalDateTime.of(APPOINTMENT_REQUEST.getDate(), APPOINTMENT_REQUEST.getTime());

        Mockito.verify(appointmentsService).deleteAppointment(CUSTOMER_ONE_ID, appointmentTimestamp);
        Mockito.verify(customersRepo).getCustomerPinById(anyLong());
    }
}
