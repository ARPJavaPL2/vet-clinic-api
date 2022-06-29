package com.simonjoz.vetclinic.service;

import com.simonjoz.vetclinic.domain.Appointment;
import com.simonjoz.vetclinic.domain.AppointmentRequest;
import com.simonjoz.vetclinic.domain.Customer;
import com.simonjoz.vetclinic.domain.Doctor;
import com.simonjoz.vetclinic.dto.AppointmentDTO;
import com.simonjoz.vetclinic.dto.CustomerDTO;
import com.simonjoz.vetclinic.dto.PageDTO;
import com.simonjoz.vetclinic.exceptions.InvalidPinException;
import com.simonjoz.vetclinic.exceptions.ResourceNotFoundException;
import com.simonjoz.vetclinic.exceptions.UnavailableDateException;
import com.simonjoz.vetclinic.mappers.PagesMapper;
import com.simonjoz.vetclinic.repository.CustomersRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class CustomersService {

    private final CustomersRepo customersRepo;
    private final PagesMapper<CustomerDTO> pagesMapper;
    private final AppointmentsService appointmentsService;
    private final DoctorsService doctorsService;

    @Cacheable("customer")
    public Customer getCustomer(Long customerId) {
        return customersRepo.findById(customerId)
                .orElseThrow(getNotFoundExceptionSupplier(customerId));
    }

    @Cacheable("customersPage")
    public PageDTO<CustomerDTO> getPage(PageRequest pageRequest) {
        Page<CustomerDTO> doctorsPage = customersRepo.getCustomersPage(pageRequest);
        return pagesMapper.map(doctorsPage);
    }

    public AppointmentDTO makeAppointment(AppointmentRequest appointmentReq, Long customerId) {
        validateIsAppointmentTimeInPast(appointmentReq.getDate(), appointmentReq.getTime());

        Customer customer = getCustomer(customerId);
        validateCustomerPin(customer.getPin(), appointmentReq.getCustomerPin());

        appointmentsService.checkDateAvailabilityForDoctor(appointmentReq);
        Doctor doctor = doctorsService.getDoctor(appointmentReq.getDoctorId());

        Appointment appointment = Appointment.builder()
                .customer(customer)
                .doctor(doctor)
                .note(appointmentReq.getNote())
                .scheduledDate(appointmentReq.getDate())
                .scheduledTime(appointmentReq.getTime())
                .timestamp(LocalDateTime.of(appointmentReq.getDate(), appointmentReq.getTime()))
                .build();

        return appointmentsService.addAppointment(appointment);
    }


    public void cancelAppointment(AppointmentRequest appointmentReq, Long customerId) {
        int customerValidPin = getCustomerPinById(customerId);
        validateCustomerPin(customerValidPin, appointmentReq.getCustomerPin());
        LocalDateTime appointmentTimestamp = LocalDateTime.of(appointmentReq.getDate(), appointmentReq.getTime());
        appointmentsService.deleteAppointment(customerId, appointmentTimestamp);
    }

    private void validateCustomerPin(int validPin, int pin) {
        if (validPin != pin) {
            throw new InvalidPinException(String.format("Given pin '%d' is invalid", pin));
        }
    }

    private int getCustomerPinById(Long customerId) {
        return customersRepo.getCustomerPinById(customerId)
                .orElseThrow(getNotFoundExceptionSupplier(customerId));
    }

    private void validateIsAppointmentTimeInPast(LocalDate date, LocalTime time) {
        LocalDate todayDate = LocalDate.now();
        if (date.isEqual(todayDate) && time.isBefore(LocalTime.now())) {
            throw new UnavailableDateException(
                    String.format("Appointment time must not be in past. Request time: '%s'.", time));
        }
    }

    private Supplier<ResourceNotFoundException> getNotFoundExceptionSupplier(long customerId) {
        return () -> new ResourceNotFoundException(String.format("Customer with id '%d' not found.", customerId));
    }


}
