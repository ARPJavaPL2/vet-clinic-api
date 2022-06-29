package com.simonjoz.vetclinic.repository;

import com.simonjoz.vetclinic.dto.DoctorDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest
@Sql(scripts = "classpath:test.sql")
class DoctorsRepoTest {

    @Autowired
    private DoctorsRepo doctorsRepo;

    private final List<DoctorDTO> doctorsList = List.of(
            new DoctorDTO(1L, "DR", "DOCTOR1", "SURNAME1"),
            new DoctorDTO(2L, "DR", "DOCTOR2", "SURNAME2"));

    @Test
    void testGetDoctorsPageSuccess() {
        PageRequest pageRequest = PageRequest.of(0, 1);
        Page<DoctorDTO> doctorsPage = doctorsRepo.getDoctorsPage(pageRequest);

        List<DoctorDTO> content = doctorsPage.getContent();

        assertFalse(doctorsPage.isEmpty());
        assertFalse(doctorsPage.isLast());
        assertTrue(doctorsPage.isFirst());
        assertEquals(1, content.size());
        assertEquals("DOCTOR1", content.get(0).getName());
        assertEquals(2, doctorsPage.getTotalPages());
        assertEquals(2, doctorsPage.getTotalElements());
    }

    @Test
    void testGetDoctorsPageAndSortByNameDescendingSuccess() {
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.Direction.DESC, "name");
        Page<DoctorDTO> doctorsPage = doctorsRepo.getDoctorsPage(pageRequest);

        List<DoctorDTO> content = doctorsPage.getContent();

        assertFalse(doctorsPage.isEmpty());
        assertTrue(doctorsPage.isFirst());
        assertTrue(doctorsPage.isLast());
        assertEquals(2, content.size());
        assertTrue(content.containsAll(doctorsList));
        assertEquals("DOCTOR2", content.get(0).getName());
        assertEquals(1, doctorsPage.getTotalPages());
        assertEquals(2, doctorsPage.getTotalElements());
    }
}
