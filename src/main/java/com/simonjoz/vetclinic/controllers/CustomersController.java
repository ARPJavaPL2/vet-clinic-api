package com.simonjoz.vetclinic.controllers;

import com.simonjoz.vetclinic.domain.AppointmentRequest;
import com.simonjoz.vetclinic.dto.AppointmentDTO;
import com.simonjoz.vetclinic.dto.CustomerDTO;
import com.simonjoz.vetclinic.dto.PageDTO;
import com.simonjoz.vetclinic.service.CustomersService;
import com.simonjoz.vetclinic.utils.PageReqUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.simonjoz.vetclinic.utils.PageReqUtils.*;

@RestController
@Api(tags = "Customers")
@RequestMapping("api/v1/customers")
@RequiredArgsConstructor
public class CustomersController {

    private final CustomersService customersService;

    @GetMapping
    @ApiOperation(value = "Fetch customers page",
            notes = "Method is used to fetch customers page. Page is sortable depend on specified params.")
    public PageDTO<CustomerDTO> getCustomersPage(
            @RequestParam(defaultValue = PAGE_ZERO, required = false) int page,
            @RequestParam(defaultValue = DEFAULT_SIZE, required = false) int pageSize,
            @RequestParam(defaultValue = DEFAULT_SORT_BY, required = false) String sortBy,
            @RequestParam(defaultValue = DESC_FALSE, required = false)
            @ApiParam(value = "Sort direction descending ?") boolean isDesc) {
        PageRequest pageRequest = PageReqUtils.getPageRequest(page, pageSize, sortBy, isDesc);
        return customersService.getPage(pageRequest);
    }

    @PostMapping("{customerId}/appointments/add")
    @ApiOperation(value = "Schedule appointment", notes = "Method is used to create new appointment " +
            "with specified doctor at certain date and time, for customer with given id. " +
            "In order to make appointment valid pin number must be provided. " +
            "Doctor availability is resolved base on visit details provided by himself. ")
    public ResponseEntity<AppointmentDTO> makeAppointment(
            @Valid @RequestBody AppointmentRequest appointmentReq, @PathVariable Long customerId) {
        var appointmentDTO = customersService.makeAppointment(appointmentReq, customerId);
        return new ResponseEntity<>(appointmentDTO, HttpStatus.CREATED);
    }

    @DeleteMapping("{customerId}/appointments/cancel")
    @ApiOperation(value = "Cancel appointment", notes = "Method is used to cancel (delete) appointment with " +
            "specified doctor at certain date and time for customer with given id. " +
            "In order to perform cancellation valid pin number must be provided.")
    public ResponseEntity<String> cancelAppointment(
            @Valid @RequestBody AppointmentRequest appointmentReq, @PathVariable Long customerId) {
        customersService.cancelAppointment(appointmentReq, customerId);
        return ResponseEntity.ok("Appointment has been removed successfully.");
    }

}
