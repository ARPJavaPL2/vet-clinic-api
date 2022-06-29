package com.simonjoz.vetclinic.repository;

import com.simonjoz.vetclinic.dto.AppointmentDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest
@Sql(scripts = "classpath:test.sql")
class AppointmentsRepoTest {

    private static final long ID_ONE = 1L;
    private static final long NONE_EXISTING_ID = 100L;
    private static final PageRequest PAGE_REQUEST = PageRequest.of(0, 1);

    @Autowired
    private AppointmentsRepo appointmentsRepo;

    @Test
    void testGetDoctorAppointmentsPageForSpecifiedDate() {
        LocalDate expectedDate = LocalDate.parse("2022-01-23");
        Page<AppointmentDTO> receivedPage =
                appointmentsRepo.getDoctorAppointmentsPage(ID_ONE, expectedDate, PAGE_REQUEST);

        assertFalse(receivedPage.isEmpty());
        assertTrue(receivedPage.isLast());
        assertTrue(receivedPage.isFirst());
        assertTrue(receivedPage.hasContent());
        assertEquals(1, receivedPage.getTotalPages());
        assertEquals(1, receivedPage.getTotalElements());
        assertEquals("APPOINTMENT3", receivedPage.getContent().get(0).getNote());
        assertEquals(expectedDate, receivedPage.getContent().get(0).getScheduledDate());
        assertEquals("CUSTOMER2", receivedPage.getContent().get(0).getPersonName());
    }

    @Test
    void testGetDoctorAppointmentsPageForSpecifiedDateNoneExistingId() {
        LocalDate expectedDate = LocalDate.parse("2022-01-23");
        Page<AppointmentDTO> receivedPage =
                appointmentsRepo.getDoctorAppointmentsPage(NONE_EXISTING_ID, expectedDate, PAGE_REQUEST);

        assertTrue(receivedPage.isEmpty());
        assertTrue(receivedPage.isLast());
        assertTrue(receivedPage.isFirst());
        assertTrue(receivedPage.getContent().isEmpty());
        assertFalse(receivedPage.hasContent());
    }

    @Test
    void testGetDoctorAppointmentsPageForDoctorById() {
        Page<AppointmentDTO> receivedPage =
                appointmentsRepo.getDoctorAppointmentsPage(ID_ONE, PAGE_REQUEST);

        assertFalse(receivedPage.isLast());
        assertFalse(receivedPage.isEmpty());
        assertTrue(receivedPage.isFirst());
        assertTrue(receivedPage.hasContent());
        assertEquals(2, receivedPage.getTotalPages());
        assertEquals(2, receivedPage.getTotalElements());
        assertEquals("APPOINTMENT1", receivedPage.getContent().get(0).getNote());
    }

    @Test
    void testGetDoctorAppointmentsPageForDoctorByIdNotFound() {
        Page<AppointmentDTO> receivedPage =
                appointmentsRepo.getDoctorAppointmentsPage(NONE_EXISTING_ID, PAGE_REQUEST);

        assertTrue(receivedPage.isEmpty());
        assertTrue(receivedPage.isLast());
        assertTrue(receivedPage.isFirst());
        assertTrue(receivedPage.getContent().isEmpty());
        assertFalse(receivedPage.hasContent());
    }


    @ParameterizedTest
    @MethodSource("customerIdAndTimestampSupplier")
    void testExistsByIdAndTimestampShouldReturnTrue(Long id, LocalDateTime timestamp) {
        assertTrue(appointmentsRepo.existsByCustomerIdAndTimestamp(id, timestamp));
    }

    @ParameterizedTest
    @MethodSource("customerIdAndTimestampSupplier")
    @Sql(scripts = "classpath:clean-up.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void testExistsByIdAndTimestampShouldReturnFalse(Long id, LocalDateTime timestamp) {
        assertFalse(appointmentsRepo.existsByCustomerIdAndTimestamp(id, timestamp));
    }

    @ParameterizedTest
    @MethodSource("customerIdAndTimestampSupplier")
    void testDeleteByCustomerIdAndTimestamp(Long id, LocalDateTime timestamp) {
        appointmentsRepo.deleteByCustomerIdAndTimestamp(id, timestamp);
        assertFalse(appointmentsRepo.existsByCustomerIdAndTimestamp(id, timestamp));
    }

    private static Stream<Arguments> customerIdAndTimestampSupplier() {
        return Stream.of(
                Arguments.of(1L, LocalDateTime.parse("2022-01-21T12:00:00")),
                Arguments.of(1L, LocalDateTime.parse("2022-01-22T12:00:00")),
                Arguments.of(2L, LocalDateTime.parse("2022-01-23T12:00:00")),
                Arguments.of(2L, LocalDateTime.parse("2022-01-24T12:00:00")));
    }

    @ParameterizedTest
    @MethodSource("timeRangeSupplierFalseValues")
    void testIsDateAndTimeAvailableForDoctorWithIdShouldReturnFalse(LocalDateTime start, LocalDateTime end) {
        /*
            Original appointment request timestamp is the middle of provided range
                e.g(start - 2022-01-21 11:30; end - 2022-01-21 12:30; original - 2022-01-21 12:00)
        */

        assertFalse(appointmentsRepo.isDateAndTimeAvailableForDoctorWithId(
                1L, start, end, LocalDateTime.parse("2022-01-21T12:00")));

        assertFalse(appointmentsRepo.isDateAndTimeAvailableForDoctorWithId(
                2L, start, end, LocalDateTime.parse("2022-01-22T12:00")));

        assertFalse(appointmentsRepo.isDateAndTimeAvailableForDoctorWithId(
                1L, start, end, LocalDateTime.parse("2022-01-23T12:00")));

        assertFalse(appointmentsRepo.isDateAndTimeAvailableForDoctorWithId(
                2L, start, end, LocalDateTime.parse("2022-01-24T12:00")));
    }

    private static Stream<Arguments> timeRangeSupplierFalseValues() {
        return Stream.of(
                Arguments.of(LocalDateTime.parse("2022-01-21T11:30"), LocalDateTime.parse("2022-01-21T12:30")),
                Arguments.of(LocalDateTime.parse("2022-01-21T11:29"), LocalDateTime.parse("2022-01-21T12:29")),
                Arguments.of(LocalDateTime.parse("2022-01-21T11:01"), LocalDateTime.parse("2022-01-21T12:01")),
                Arguments.of(LocalDateTime.parse("2022-01-21T11:20"), LocalDateTime.parse("2022-01-21T12:20")),
                Arguments.of(LocalDateTime.parse("2022-01-21T11:10"), LocalDateTime.parse("2022-01-21T12:10")),
                Arguments.of(LocalDateTime.parse("2022-01-21T11:05"), LocalDateTime.parse("2022-01-21T12:05")));
    }

    @ParameterizedTest
    @MethodSource("timeRangeSupplierTrueValues")
    void testIsDateAndTimeAvailableForDoctorWithIdShouldReturnTrue(LocalDateTime start, LocalDateTime end) {
        /*
            Original appointment request timestamp is the middle of provided range
                e.g(start - 2022-01-21 11:30; end - 2022-01-21 12:30; original - 2022-01-21 12:00)
        */
        assertTrue(appointmentsRepo.isDateAndTimeAvailableForDoctorWithId(
                1L, start, end, LocalDateTime.parse("2022-01-21T12:30")));

        assertTrue(appointmentsRepo.isDateAndTimeAvailableForDoctorWithId(
                2L, start, end, LocalDateTime.parse("2022-01-22T12:30")));

        assertTrue(appointmentsRepo.isDateAndTimeAvailableForDoctorWithId(
                1L, start, end, LocalDateTime.parse("2022-01-23T12:30")));

        assertTrue(appointmentsRepo.isDateAndTimeAvailableForDoctorWithId(
                2L, start, end, LocalDateTime.parse("2022-01-24T12:30")));

    }

    private static Stream<Arguments> timeRangeSupplierTrueValues() {
        return Stream.of(
                Arguments.of(LocalDateTime.parse("2022-01-21T12:00"), LocalDateTime.parse("2022-01-21T13:00")),
                Arguments.of(LocalDateTime.parse("2022-01-21T12:35"), LocalDateTime.parse("2022-01-21T13:35")),
                Arguments.of(LocalDateTime.parse("2022-01-21T12:20"), LocalDateTime.parse("2022-01-21T13:20")),
                Arguments.of(LocalDateTime.parse("2022-01-21T12:29"), LocalDateTime.parse("2022-01-21T13:29")),
                Arguments.of(LocalDateTime.parse("2022-01-21T13:00"), LocalDateTime.parse("2022-01-21T14:00")),
                Arguments.of(LocalDateTime.parse("2022-01-21T12:01"), LocalDateTime.parse("2022-01-21T13:01")));
    }

}
