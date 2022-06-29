package com.simonjoz.vetclinic.repository;

import com.simonjoz.vetclinic.domain.VisitDetails;
import com.simonjoz.vetclinic.dto.TimingDetailsDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface VisitDetailsRepo extends JpaRepository<VisitDetails, Long> {

    @Query("SELECT new com.simonjoz.vetclinic.dto.TimingDetailsDTO(v.visitDurationInMinutes," +
            " v.openingAt, v.closingAt) FROM VisitDetails v WHERE v.id = :doctorId")
    Optional<TimingDetailsDTO> getTimingDetails(Long doctorId);

}
