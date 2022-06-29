package com.simonjoz.vetclinic.repository;

import com.simonjoz.vetclinic.domain.Doctor;
import com.simonjoz.vetclinic.dto.DoctorDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface DoctorsRepo extends JpaRepository<Doctor, Long> {

    @Query("SELECT new com.simonjoz.vetclinic.dto.DoctorDTO(d.id, d.title, d.name, d.surname) FROM doctors d")
    Page<DoctorDTO> getDoctorsPage(Pageable pageable);
}
