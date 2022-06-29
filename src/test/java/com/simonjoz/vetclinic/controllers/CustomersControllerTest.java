package com.simonjoz.vetclinic.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simonjoz.vetclinic.domain.AppointmentRequest;
import com.simonjoz.vetclinic.dto.CustomerDTO;
import com.simonjoz.vetclinic.dto.PageDTO;
import com.simonjoz.vetclinic.exceptions.InvalidPinException;
import com.simonjoz.vetclinic.service.CustomersService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(CustomersController.class)
class CustomersControllerTest {

    private static final String CUSTOMERS_MAPPING = "/api/v1/customers/";

    private static final AppointmentRequest APPOINTMENT_REQUEST = new AppointmentRequest(1234, 1L,
            "some note here", LocalDate.now(), LocalTime.now());

    @MockBean
    private CustomersService customersService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @AfterEach
    void reset() {
        Mockito.reset(customersService);
    }

    @Test
    void tesGetCustomersPage() throws Exception {
        PageDTO<CustomerDTO> expectedResultPage = new PageDTO<>(1, 1, true, true, false,
                List.of(new CustomerDTO(1L, 1234, "CUSTOMER1", "SURNAME1")));

        Mockito.doReturn(expectedResultPage).when(customersService).getPage(any(PageRequest.class));

        String resultPageJson = objectMapper.writeValueAsString(expectedResultPage);

        mockMvc.perform(get(CUSTOMERS_MAPPING))
                .andExpect(status().isOk())
                .andExpect(content().string(resultPageJson));

        Mockito.verify(customersService).getPage(any(PageRequest.class));
    }

    @Test
    void testMakeAppointment() throws Exception {
        String requestBody = objectMapper.writeValueAsString(APPOINTMENT_REQUEST);

        mockMvc.perform(post(CUSTOMERS_MAPPING + "/1/appointments/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated());

        Mockito.verify(customersService).makeAppointment(any(AppointmentRequest.class), anyLong());
    }

    @Test
    void testMakeAppointmentInvalidPin() throws Exception {

        Mockito.doThrow(InvalidPinException.class).when(customersService)
                .makeAppointment(any(AppointmentRequest.class), anyLong());

        String requestBody = objectMapper.writeValueAsString(APPOINTMENT_REQUEST);

        mockMvc.perform(post(CUSTOMERS_MAPPING + "/1/appointments/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest());

        Mockito.verify(customersService).makeAppointment(any(AppointmentRequest.class), anyLong());
    }

    @Test
    void testCancelAppointment() throws Exception {

        String requestBody = objectMapper.writeValueAsString(APPOINTMENT_REQUEST);
        mockMvc.perform(delete(CUSTOMERS_MAPPING + "/1/appointments/cancel")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().string("Appointment has been removed successfully."));

        Mockito.verify(customersService).cancelAppointment(any(AppointmentRequest.class), anyLong());
    }


    @Test
    void testCancelAppointmentInvalidPin() throws Exception {

        Mockito.doThrow(InvalidPinException.class).when(customersService)
                .cancelAppointment(any(AppointmentRequest.class), anyLong());

        String requestBody = objectMapper.writeValueAsString(APPOINTMENT_REQUEST);

        mockMvc.perform(delete(CUSTOMERS_MAPPING + "/1/appointments/cancel")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest());

        Mockito.verify(customersService).cancelAppointment(any(AppointmentRequest.class), anyLong());
    }
}
