package ru.practicum.shareit.util;

import org.springframework.data.domain.Sort;

public class UtilConstants {

    public static final String OWNER_ID_HEADER = "X-Sharer-User-Id";
    public static final String DATE_TIME_PATTERN = "yyyy-MM-dd'T'hh:mm:ss";
    public static final String DEFAULT_FROM = "0";
    public static final String DEFAULT_LIMIT = "10";

    public static final String USER_PATH = "/users";

    public static final String ITEM_PATH = "/items";
    public static final Sort ITEM_SORT = Sort.by("id").ascending();

    public static final String BOOKING_PATH = "/bookings";
    public static final Sort BOOKING_SORT = Sort.by("start").descending();

    public static final String REQUESTS_PATH = "/requests";
}
