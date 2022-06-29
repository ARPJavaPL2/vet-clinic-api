package com.simonjoz.vetclinic.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VisitDetails {

    @Id
    private Long id;
    private BigDecimal visitPrice;

    @Positive(message = "Visit duration time must be positive number.")
    private int visitDurationInMinutes;
    private LocalTime openingAt;
    private LocalTime closingAt;

    @MapsId
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToOne(fetch = FetchType.LAZY)
    @NotNull(message = "Doctor is required.")
    private Doctor doctor;
}
