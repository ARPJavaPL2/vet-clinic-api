package com.simonjoz.vetclinic.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@AllArgsConstructor
public class AppointmentRequest {

    @Min(value = 4, message = "Pin number must not be null and less than four digits.")
    private int customerPin;

    @NotNull(message = "Doctors ID is required.")
    private Long doctorId;

    private String note;

    @NotNull(message = "Date is required.")
    @FutureOrPresent(message = "Date value must be in future or present.")
    private LocalDate date;

    @NotNull(message = "Time is required.")
    private LocalTime time;
}
