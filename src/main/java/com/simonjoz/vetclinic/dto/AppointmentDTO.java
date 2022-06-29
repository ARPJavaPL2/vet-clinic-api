package com.simonjoz.vetclinic.dto;

import lombok.Value;

import java.time.LocalDate;
import java.time.LocalTime;

@Value
public class AppointmentDTO {
    Long id;
    String note;
    LocalDate scheduledDate;
    LocalTime scheduledTime;
    String personName;
    String personSurname;
}
