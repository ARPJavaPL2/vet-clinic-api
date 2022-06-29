package com.simonjoz.vetclinic.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class PageReqUtils {

    public final static String PAGE_ZERO = "0";
    public final static String DEFAULT_SORT_BY = "id";
    public final static String DEFAULT_SIZE = "10";
    public final static String DESC_FALSE = "false";


    public static PageRequest getPageRequest(int page, int size, String sortBy, boolean desc) {
        PageRequest pageRequest = PageRequest.of(page, size);
        if (sortBy != null && !"".equals(sortBy.trim())) {
            if (desc) {
                pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, sortBy));
            } else {
                pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, sortBy));
            }
        }
        return pageRequest;
    }
}
