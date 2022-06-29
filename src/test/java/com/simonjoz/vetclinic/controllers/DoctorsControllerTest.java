package com.simonjoz.vetclinic.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simonjoz.vetclinic.dto.AppointmentDTO;
import com.simonjoz.vetclinic.dto.DoctorDTO;
import com.simonjoz.vetclinic.dto.PageDTO;
import com.simonjoz.vetclinic.service.DoctorsService;
import com.simonjoz.vetclinic.utils.PageReqUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(DoctorsController.class)
class DoctorsControllerTest {

    private static final String DOCTORS_MAPPING = "/api/v1/doctors";

    @MockBean
    private DoctorsService doctorsService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @AfterEach
    void reset() {
        Mockito.reset(doctorsService);
    }

    @Test
    void testGetDoctorsPage() throws Exception {
        PageDTO<DoctorDTO> expectedResultPage = new PageDTO<>(1, 1, true, true, false,
                List.of(new DoctorDTO(1L, "DR", "DOCTOR1", "SURNAME1")));

        Mockito.doReturn(expectedResultPage).when(doctorsService).getPage(any(PageRequest.class));

        String resultPageJson = objectMapper.writeValueAsString(expectedResultPage);

        mockMvc.perform(get(DOCTORS_MAPPING))
                .andExpect(status().isOk())
                .andExpect(content().string(resultPageJson));

        Mockito.verify(doctorsService).getPage(any(PageRequest.class));
    }

    @Test
    void testGetAppointmentsPageByDoctorIdSuccess() throws Exception {

        PageRequest pageRequest = PageReqUtils.getPageRequest(0, 10, "id", false);
        PageDTO<AppointmentDTO> expectedResultPage = new PageDTO<>(1, 1, true, true,
                false, List.of(new AppointmentDTO(1L, "APPOINTMENT 1 NOTE", LocalDate.now(), LocalTime.now(),
                "CUSTOMER1", "SURNAME1")));

        Mockito.when(doctorsService.getAppointmentsPageById(pageRequest, 1L, null))
                .thenReturn(expectedResultPage);

        String resultJson = objectMapper.writeValueAsString(expectedResultPage);
        mockMvc.perform(get(DOCTORS_MAPPING + "/1/appointments")
                .param("page", "0")
                .param("pageSize", "10")
                .param("sortBy", "id")
                .param("isDesc", "false"))
                .andExpect(status().isOk())
                .andExpect(content().string(resultJson));
        Mockito.verify(doctorsService).getAppointmentsPageById(pageRequest, 1L, null);

        Mockito.reset(doctorsService);
        LocalDate date = LocalDate.parse("2022-10-10");
        Mockito.when(doctorsService.getAppointmentsPageById(pageRequest, 1L, date))
                .thenReturn(expectedResultPage);

        mockMvc.perform(get(DOCTORS_MAPPING + "/1/appointments")
                .param("page", "0")
                .param("pageSize", "10")
                .param("sortBy", "id")
                .param("isDesc", "false")
                .param("date", "2022-10-10"))
                .andExpect(status().isOk())
                .andExpect(content().string(resultJson));

        Mockito.verify(doctorsService).getAppointmentsPageById(pageRequest, 1L, date);
    }


    @ParameterizedTest
    @ValueSource(strings = {"-1", "-10", "-21"})
    void testGetAppointmentsPageByDoctorIdInvalidPageNumber(String page) throws Exception {
        mockMvc.perform(get(DOCTORS_MAPPING + "/1/appointments")
                .param("page", page))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Page index must not be less than zero!"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "-1", "-234"})
    void testGetAppointmentsPageByDoctorIdInvalidSize(String size) throws Exception {
        mockMvc.perform(get(DOCTORS_MAPPING + "/1/appointments")
                .param("pageSize", size))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Page size must not be less than one!"));
    }

}
