package com.simonjoz.vetclinic.repository;

import com.simonjoz.vetclinic.domain.Customer;
import com.simonjoz.vetclinic.dto.CustomerDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CustomersRepo extends JpaRepository<Customer, Long> {

    @Query("SELECT new com.simonjoz.vetclinic.dto.CustomerDTO(c.id, c.pin, c.name, c.surname) FROM customers c")
    Page<CustomerDTO> getCustomersPage(PageRequest pageRequest);

    @Query("SELECT c.pin FROM customers c WHERE c.id = :customerId")
    Optional<Integer> getCustomerPinById(Long customerId);
}
