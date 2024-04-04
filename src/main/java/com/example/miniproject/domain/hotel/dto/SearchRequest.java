package com.example.miniproject.domain.hotel.dto;

import com.example.miniproject.domain.hotel.constant.Nation;
import com.example.miniproject.domain.hotel.constant.RoomType;
import com.example.miniproject.domain.hotel.constant.SearchType;
import com.example.miniproject.domain.hotel.constant.ViewType;
import lombok.Data;
import org.springframework.data.domain.Pageable;


@Data
public class SearchRequest {

    private SearchType searchType;
    private String email;
    private Long id;
    private Nation nation;
    private RoomType roomType;
    private ViewType viewType;
    private String name;
    private Pageable pageable;

    private SearchRequest() {

    }

    public SearchRequest(SearchType searchType) {
        this.searchType = searchType;
    }

}
