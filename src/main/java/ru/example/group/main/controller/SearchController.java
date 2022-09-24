package ru.example.group.main.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.example.group.main.dto.response.CommonListResponseDto;
import ru.example.group.main.service.SearchService;

@RestController
public class SearchController {

    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping("/api/v1/users/search")
    public ResponseEntity<CommonListResponseDto<Object>> usersSearch(
            @RequestParam(defaultValue = "") String first_name,
            @RequestParam(defaultValue = "") String last_name,
            @RequestParam(defaultValue = "-1") Long age_from,
            @RequestParam(defaultValue = "-1") Long age_to,
            @RequestParam(defaultValue = "") String country,
            @RequestParam(defaultValue = "") String city,
            @RequestParam(defaultValue = "0") Integer offset,
            @RequestParam(defaultValue = "20") Integer itemPerPage
    ) {
        return new ResponseEntity<>(searchService.userSearch(
                first_name, last_name, age_from, age_to,
                country, city, offset, itemPerPage
        ), HttpStatus.OK);
    }

    @GetMapping("/api/v1/post")
    public ResponseEntity<CommonListResponseDto<Object>> postSearch(
            @RequestParam(defaultValue = "") String text,
            @RequestParam(defaultValue = "-1") Long date_from,
            @RequestParam(defaultValue = "-1") Long date_to,
            @RequestParam(defaultValue = "0") Integer offset,
            @RequestParam(defaultValue = "20") Integer itemPerPage,
            @RequestParam(defaultValue = "") String author,
            @RequestParam(defaultValue = "") String tag
    ) {
        return new ResponseEntity<>(searchService.postSearch(text, date_from, date_to,
                offset, itemPerPage, author, tag), HttpStatus.OK);
    }

}
