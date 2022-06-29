package com.simonjoz.vetclinic.domain;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "appointments")
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // on db side.
    private Long id;

    private String note;

    @NotNull(message = "Date is required.")
    @FutureOrPresent(message = "Date value must be in future or present.")
    private LocalDate scheduledDate;

    @NotNull(message = "Time is required.")
    private LocalTime scheduledTime;

    @Column(unique = true)
    @NotNull(message = "Timestamp must be provided")
    private LocalDateTime timestamp;

    // ManyToOne -- eager by default.
    // Lazy fetch will cause jackson to fail on empty bean.
    // NOTE: In case of fetching entity directly without mapping to DTO - n + 1 issue need to be handled.

    @ManyToOne
    @JoinColumn(name = "customer_id")
    @NotNull(message = "Customer is required")
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "doctor_id")
    @NotNull(message = "Doctor is required")
    private Doctor doctor;
}
