package com.simonjoz.vetclinic.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "doctors")
@ToString(exclude = "appointments")
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @NotBlank(message = "Name is required.")
    private String name;

    @NotBlank(message = "Surname is required.")
    private String surname;

    @OneToOne(mappedBy = "doctor")
    private VisitDetails visitDetails;

    @JsonIgnore
    @Builder.Default
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "doctor")
    private List<Appointment> appointments = new ArrayList<>();
}
