package com.simonjoz.vetclinic.mappers;


import com.simonjoz.vetclinic.dto.PageDTO;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PagesMapper<T> {

    public PageDTO<T> map(Page<T> page) {
        List<T> content = page.getContent();
        long totalElements = page.getTotalElements();
        int totalPages = page.getTotalPages();
        boolean first = page.isFirst();
        boolean last = page.isLast();
        boolean empty = page.isEmpty();
        return new PageDTO<>(totalPages, totalElements, first, last, empty, content);
    }

}
