package com.simonjoz.vetclinic.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "customers")
@ToString(exclude = "appointments")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Min(value = 4, message = "Pin must not be null and less than 4 digits.")
    private int pin;

    @NotBlank(message = "Name is required.")
    private String name;

    @NotBlank(message = "Surname is required.")
    private String surname;

    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "customer") // lazy by default
    private List<Appointment> appointments = new ArrayList<>();
}
