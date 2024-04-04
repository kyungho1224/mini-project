package com.example.miniproject.domain.hotel.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum SearchType {

    ALL_AUTHENTICATION,
    ALL_ANONYMOUS,
    NATION_AUTHENTICATION,
    NATION_ANONYMOUS,
    NAME_AUTHENTICATION,
    NAME_ANONYMOUS,
    NAME_AND_NATION_AUTHENTICATION,
    NAME_AND_NATION_ANONYMOUS,
    SEARCH_AUTHENTICATION,
    SEARCH_ANONYMOUS

}
