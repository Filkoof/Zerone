package ru.example.group.main.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.example.group.main.dto.response.CommonListResponseDto;
import ru.example.group.main.exception.PostsException;
import ru.example.group.main.service.SearchService;

@RequiredArgsConstructor
@RestController
public class SearchController {
    private final SearchService searchService;

    @GetMapping("/api/v1/users/search")
    public ResponseEntity<CommonListResponseDto<Object>> usersSearch(
            @RequestParam(value = "first_name", defaultValue = "") String firstName,
            @RequestParam(value = "last_name", defaultValue = "") String lastName,
            @RequestParam(value = "age_from", defaultValue = "-1") Long ageFrom,
            @RequestParam(value = "age_to", defaultValue = "-1") Long ageTo,
            @RequestParam(value = "country", defaultValue = "") String country,
            @RequestParam(value = "city", defaultValue = "") String city,
            @RequestParam(defaultValue = "0") Integer offset,
            @RequestParam(defaultValue = "20") Integer itemPerPage
    ) {
        return new ResponseEntity<>(searchService.userSearch(
                firstName, lastName, ageFrom, ageTo,
                country, city, offset, itemPerPage
        ), HttpStatus.OK);
    }

    @GetMapping("/api/v1/post")
    public ResponseEntity<CommonListResponseDto<Object>> postSearch(
            @RequestParam(defaultValue = "") String text,
            @RequestParam(value = "date_from", defaultValue = "-1") Long dateFrom,
            @RequestParam(value = "date_to", defaultValue = "-1") Long dateTo,
            @RequestParam(defaultValue = "0") Integer offset,
            @RequestParam(defaultValue = "20") Integer itemPerPage,
            @RequestParam(defaultValue = "") String author,
            @RequestParam(defaultValue = "") String tag
    ) throws PostsException {
        return new ResponseEntity<>(searchService.postSearch(text, dateFrom, dateTo,
                offset, author, tag), HttpStatus.OK);
    }
}
