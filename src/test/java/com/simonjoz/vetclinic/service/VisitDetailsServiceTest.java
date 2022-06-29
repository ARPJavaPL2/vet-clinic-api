package com.simonjoz.vetclinic.service;

import com.simonjoz.vetclinic.dto.TimingDetailsDTO;
import com.simonjoz.vetclinic.exceptions.ResourceNotFoundException;
import com.simonjoz.vetclinic.repository.VisitDetailsRepo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;

@SpringBootTest
class VisitDetailsServiceTest {

    @Autowired
    private VisitDetailsService visitDetailsService;

    @MockBean
    private VisitDetailsRepo visitDetailsRepo;

    @AfterEach
    void reset() {
        Mockito.reset(visitDetailsRepo);
    }

    @Test
    void testGetTimingDetailsSuccess() {
        LocalTime closingAt = LocalTime.of(16, 0);
        LocalTime openingAt = LocalTime.of(8, 0);
        int visitDurationInMinutes = 30;

        TimingDetailsDTO expectedDetails = new TimingDetailsDTO(visitDurationInMinutes, openingAt, closingAt);
        Mockito.doReturn(Optional.of(expectedDetails)).when(visitDetailsRepo).getTimingDetails(anyLong());
        TimingDetailsDTO actualDetails = visitDetailsService.getTimingDetails(1L);

        assertEquals(expectedDetails, actualDetails);
        assertEquals(expectedDetails.getOpeningAt(), actualDetails.getOpeningAt());
        assertEquals(expectedDetails.getClosingAt(), actualDetails.getClosingAt());
        assertEquals(expectedDetails.getVisitDurationInMinutes(), actualDetails.getVisitDurationInMinutes());

        Mockito.verify(visitDetailsRepo).getTimingDetails(anyLong());
    }

    @Test
    void testGetTimingDetailsNotFound() {
        assertThrows(ResourceNotFoundException.class, () -> {
            long noneExistingId = 1L;
            visitDetailsService.getTimingDetails(noneExistingId);
        });
    }
}
