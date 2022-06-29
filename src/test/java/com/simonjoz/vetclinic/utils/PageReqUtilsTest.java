package com.simonjoz.vetclinic.utils;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class PageReqUtilsTest {

    private static Stream<Arguments> pageReqSupplier() {
        return Stream.of(
                Arguments.of(0, 10, "id", false, PageRequest.of(0, 10, Sort.Direction.ASC, "id")),
                Arguments.of(1, 20, "surname", false, PageRequest.of(1, 20, Sort.Direction.ASC, "surname")),
                Arguments.of(4, 100, "address", true, PageRequest.of(4, 100, Sort.Direction.DESC, "address")),
                Arguments.of(132, 8, "postCode", false, PageRequest.of(132, 8, Sort.Direction.ASC, "postCode")),
                Arguments.of(12, 532, "phoneNo", true, PageRequest.of(12, 532, Sort.Direction.DESC, "phoneNo")));
    }

    @ParameterizedTest
    @MethodSource("pageReqSupplier")
    void testGetPageRequestSuccess(int page, int size, String sortBy, boolean isDesc, PageRequest expectedReq) {
        PageRequest actualReq = PageReqUtils.getPageRequest(page, size, sortBy, isDesc);
        assertEquals(expectedReq, actualReq);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -2312, 0, -7, Integer.MIN_VALUE})
    void testGetPageRequestInvalidSize(int size) {
        RuntimeException ex = assertThrows(IllegalArgumentException.class,
                () -> PageReqUtils.getPageRequest(0, size, "id", false));
        assertEquals("Page size must not be less than one!", ex.getMessage());

    }

    @ParameterizedTest
    @ValueSource(ints = {-7, -1, -2312, -4, -7, Integer.MIN_VALUE})
    void testGetPageRequestInvalidPage(int page) {
        RuntimeException ex = assertThrows(IllegalArgumentException.class,
                () -> PageReqUtils.getPageRequest(page, 10, "id", false));
        assertEquals("Page index must not be less than zero!", ex.getMessage());
    }

}
