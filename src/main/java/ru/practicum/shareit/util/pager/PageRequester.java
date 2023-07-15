package ru.practicum.shareit.util.pager;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class PageRequester extends PageRequest {

    public PageRequester(int from, int limit, Sort sort) {
        super(from / limit, limit, sort);
    }
}
