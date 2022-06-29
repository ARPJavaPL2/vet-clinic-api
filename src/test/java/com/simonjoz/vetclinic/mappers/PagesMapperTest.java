package com.simonjoz.vetclinic.mappers;

import com.simonjoz.vetclinic.dto.AppointmentDTO;
import com.simonjoz.vetclinic.dto.CustomerDTO;
import com.simonjoz.vetclinic.dto.DoctorDTO;
import com.simonjoz.vetclinic.dto.PageDTO;
import com.simonjoz.vetclinic.repository.AppointmentsRepo;
import com.simonjoz.vetclinic.repository.CustomersRepo;
import com.simonjoz.vetclinic.repository.DoctorsRepo;
import com.simonjoz.vetclinic.utils.PageReqUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@SqlGroup({
        @Sql(scripts = "classpath:test.sql"),
        @Sql(scripts = "classpath:clean-up.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
})
class PagesMapperTest {

    private static final long ID_ONE = 1L;
    private static final int PAGE_ZERO = 0;
    private static final int SIZE_ONE = 1;


    @Autowired
    private DoctorsRepo doctorsRepo;

    @Autowired
    private CustomersRepo customersRepo;

    @Autowired
    private AppointmentsRepo appointmentsRepo;

    @Autowired
    private PagesMapper<DoctorDTO> doctorsPageMapper;

    @Autowired
    private PagesMapper<CustomerDTO> customersPageMapper;

    @Autowired
    private PagesMapper<AppointmentDTO> customersAppointmentPageMapper;


    @Test
    void testMapDoctorsSuccess() {
        final List<DoctorDTO> doctorsList =
                List.of(new DoctorDTO(ID_ONE, "DR", "DOCTOR1", "SURNAME1"));

        final PageDTO<DoctorDTO> expectedPage =
                new PageDTO<>(2, 2, true, false, false, doctorsList);

        PageRequest pageRequest = PageReqUtils.getPageRequest(PAGE_ZERO, SIZE_ONE, null, false);
        Page<DoctorDTO> doctorsPage = doctorsRepo.getDoctorsPage(pageRequest);
        PageDTO<DoctorDTO> actualPage = doctorsPageMapper.map(doctorsPage);

        assertFalse(actualPage.isEmpty());
        assertFalse(actualPage.isLast());
        assertTrue(actualPage.isFirst());
        assertEquals(expectedPage, actualPage);
        assertEquals(2, actualPage.getTotalElements());
        assertEquals(2, actualPage.getTotalPages());
    }

    @Test
    void testMapCustomerSuccess() {

        final List<CustomerDTO> customersList =
                List.of(new CustomerDTO(ID_ONE, 1234, "CUSTOMER1", "SURNAME1"));

        final PageDTO<CustomerDTO> expectedPage =
                new PageDTO<>(2, 2, true, false, false, customersList);

        PageRequest pageRequest = PageReqUtils.getPageRequest(PAGE_ZERO, SIZE_ONE, null, false);
        Page<CustomerDTO> doctorsPage = customersRepo.getCustomersPage(pageRequest);
        PageDTO<CustomerDTO> actualPage = customersPageMapper.map(doctorsPage);

        assertFalse(actualPage.isEmpty());
        assertFalse(actualPage.isLast());
        assertTrue(actualPage.isFirst());
        assertEquals(expectedPage, actualPage);
        assertEquals(2, actualPage.getTotalElements());
        assertEquals(2, actualPage.getTotalPages());
    }


    @Test
    void testMapAppointmentsSuccess() {
        final List<AppointmentDTO> appointmentsList =
                List.of(new AppointmentDTO(ID_ONE, "APPOINTMENT1", LocalDate.parse("2022-01-21"),
                        LocalTime.of(12, 0), "CUSTOMER1", "SURNAME1"));

        final PageDTO<AppointmentDTO> expectedPage =
                new PageDTO<>(2, 2, true, false, false, appointmentsList);

        PageRequest pageRequest = PageReqUtils.getPageRequest(PAGE_ZERO, SIZE_ONE, null, false);
        Page<AppointmentDTO> page = appointmentsRepo.getDoctorAppointmentsPage(ID_ONE, pageRequest);
        PageDTO<AppointmentDTO> actualPage = customersAppointmentPageMapper.map(page);

        assertFalse(actualPage.isEmpty());
        assertFalse(actualPage.isLast());
        assertTrue(actualPage.isFirst());
        assertEquals(expectedPage, actualPage);
        assertEquals(2, actualPage.getTotalElements());
        assertEquals(2, actualPage.getTotalPages());
    }
}
