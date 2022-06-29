package com.simonjoz.vetclinic.repository;

import com.simonjoz.vetclinic.dto.TimingDetailsDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalTime;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Sql(scripts = "classpath:test.sql")
class VisitDetailsRepoTest {

    @Autowired
    private VisitDetailsRepo visitDetailsRepo;


    @ParameterizedTest
    @MethodSource("timesDetailsSupplier")
    void testGetTimingDetailsSuccess(int duration, LocalTime openingAt, LocalTime closingAt, Long doctorId) {
        TimingDetailsDTO expectedDTO = new TimingDetailsDTO(duration, openingAt, closingAt);
        TimingDetailsDTO actualDTO = visitDetailsRepo.getTimingDetails(doctorId).orElse(null);

        assertNotNull(actualDTO);
        assertEquals(expectedDTO, actualDTO);
        assertEquals(expectedDTO.getOpeningAt(), actualDTO.getOpeningAt());
        assertEquals(expectedDTO.getClosingAt(), actualDTO.getClosingAt());
        assertEquals(expectedDTO.getVisitDurationInMinutes(), actualDTO.getVisitDurationInMinutes());
    }

    /*
    sql data
    (150, 30, '08:00', '16:00', 1) - doctor 1
    (210, 60, '16:00', '00:00', 2) - doctor 2
    */
    private static Stream<Arguments> timesDetailsSupplier() {
        return Stream.of(
                Arguments.of(30, LocalTime.of(8, 0), LocalTime.of(16, 0), 1L),
                Arguments.of(60, LocalTime.of(16, 0), LocalTime.of(0, 0), 2L));
    }

    @Test
    void testGetTimingDetailsNotFound() {
        long noneExistingId = 100L;
        Optional<TimingDetailsDTO> timingDetails = visitDetailsRepo.getTimingDetails(noneExistingId);
        assertTrue(timingDetails.isEmpty());
    }
}
