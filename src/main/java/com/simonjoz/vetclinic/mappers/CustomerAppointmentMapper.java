package com.simonjoz.vetclinic.mappers;

import com.simonjoz.vetclinic.domain.Appointment;
import com.simonjoz.vetclinic.dto.AppointmentDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CustomerAppointmentMapper {

    @Mapping(source = "doctor.name", target = "personName")
    @Mapping(source = "doctor.surname", target = "personSurname")
    AppointmentDTO map(Appointment appointment);

}
