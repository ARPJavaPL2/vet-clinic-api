package com.simonjoz.vetclinic.controllers;

import com.simonjoz.vetclinic.dto.AppointmentDTO;
import com.simonjoz.vetclinic.dto.DoctorDTO;
import com.simonjoz.vetclinic.dto.PageDTO;
import com.simonjoz.vetclinic.service.DoctorsService;
import com.simonjoz.vetclinic.utils.PageReqUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;

import static com.simonjoz.vetclinic.utils.PageReqUtils.*;

@Api(tags = "Doctors")
@RestController
@RequestMapping("api/v1/doctors")
@RequiredArgsConstructor
public class DoctorsController {

    private final DoctorsService doctorsService;

    @GetMapping
    @ApiOperation(value = "Fetch doctors list",
            notes = "Method is used to fetch doctors page. Page is sortable depend on specified params.")
    public PageDTO<DoctorDTO> getDoctorsPage(
            @RequestParam(defaultValue = PAGE_ZERO, required = false) int page,
            @RequestParam(defaultValue = DEFAULT_SIZE, required = false) int pageSize,
            @RequestParam(defaultValue = DEFAULT_SORT_BY, required = false) String sortBy,
            @RequestParam(defaultValue = DESC_FALSE, required = false)
            @ApiParam(value = "Sort direction descending ?") boolean isDesc) {
        PageRequest pageRequest = PageReqUtils.getPageRequest(page, pageSize, sortBy, isDesc);
        return doctorsService.getPage(pageRequest);
    }


    @GetMapping("{doctorId}/appointments")
    @ApiOperation(value = "Fetch all appointments by doctor id",
            notes = "Method is used to fetch appointments page for doctor with specified id. " +
                    "Method takes optional argument of date (yyyy-MM-dd) in order to narrow result to certain period. " +
                    "It is also sortable depend on specified params.")
    public PageDTO<AppointmentDTO> getAppointmentsPageByDoctorId(
            @RequestParam(defaultValue = PAGE_ZERO, required = false) int page,
            @RequestParam(defaultValue = DEFAULT_SIZE, required = false) int pageSize,
            @RequestParam(defaultValue = DEFAULT_SORT_BY, required = false) String sortBy,
            @RequestParam(defaultValue = DESC_FALSE, required = false)
            @ApiParam(value = "Sort direction descending ?") boolean isDesc,
            @Valid @RequestParam(required = false)
            @ApiParam(format = "yyyy-MM-dd", example = "2022-01-23", value = "Appointments date") LocalDate date,
            @PathVariable Long doctorId) {
        PageRequest pageRequest = PageReqUtils.getPageRequest(page, pageSize, sortBy, isDesc);
        return doctorsService.getAppointmentsPageById(pageRequest, doctorId, date);
    }
}
