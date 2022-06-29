package com.simonjoz.vetclinic.repository;

import com.simonjoz.vetclinic.dto.CustomerDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Sql(scripts = "classpath:test.sql")
class CustomersRepoTest {

    @Autowired
    private CustomersRepo customersRepo;


    private final List<CustomerDTO> doctorsList = List.of(
            new CustomerDTO(1L, 1234, "CUSTOMER1", "SURNAME1"),
            new CustomerDTO(2L, 1234, "CUSTOMER2", "SURNAME2"));


    @Test
    void testGetCustomersPage() {
        PageRequest pageRequest = PageRequest.of(0, 1);
        Page<CustomerDTO> customersPage = customersRepo.getCustomersPage(pageRequest);

        List<CustomerDTO> content = customersPage.getContent();

        assertFalse(customersPage.isEmpty());
        assertFalse(customersPage.isLast());
        assertTrue(customersPage.isFirst());
        assertEquals(1, content.size());
        assertEquals("CUSTOMER1", content.get(0).getName());
        assertEquals(2, customersPage.getTotalPages());
        assertEquals(2, customersPage.getTotalElements());
    }


    @Test
    void testGetDoctorsPageAndSortByNameDescendingSuccess() {
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.Direction.DESC, "name");
        Page<CustomerDTO> doctorsPage = customersRepo.getCustomersPage(pageRequest);

        List<CustomerDTO> content = doctorsPage.getContent();

        assertFalse(doctorsPage.isEmpty());
        assertTrue(doctorsPage.isFirst());
        assertTrue(doctorsPage.isLast());
        assertEquals(2, content.size());
        assertTrue(content.containsAll(doctorsList));
        assertEquals("CUSTOMER2", content.get(0).getName());
        assertEquals(1, doctorsPage.getTotalPages());
        assertEquals(2, doctorsPage.getTotalElements());
    }

    @ParameterizedTest
    @ValueSource(longs = {1, 2})
    void testGetCustomerPinByIdSuccess(Long customerId) {
        Integer customerPinById = customersRepo.getCustomerPinById(customerId).orElse(null);
        assertEquals(1234, customerPinById);
    }

    @ParameterizedTest
    @ValueSource(longs = {Long.MIN_VALUE, 0, 5, Long.MAX_VALUE})
    void testGetCustomerPinByIdNoneExistingId(Long customerId) {
        Optional<Integer> customerPinById = customersRepo.getCustomerPinById(customerId);
        assertEquals(Optional.empty(), customerPinById);
    }

}
