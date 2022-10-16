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
            @RequestParam(defaultValue = "") String firstName,
            @RequestParam(defaultValue = "") String lastName,
            @RequestParam(defaultValue = "-1") Long ageFrom,
            @RequestParam(defaultValue = "-1") Long ageTo,
            @RequestParam(defaultValue = "") String country,
            @RequestParam(defaultValue = "") String city,
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
            @RequestParam(defaultValue = "-1") Long dateFrom,
            @RequestParam(defaultValue = "-1") Long dateTo,
            @RequestParam(defaultValue = "0") Integer offset,
            @RequestParam(defaultValue = "20") Integer itemPerPage,
            @RequestParam(defaultValue = "") String author,
            @RequestParam(defaultValue = "") String tag
    ) throws PostsException {
        return new ResponseEntity<>(searchService.postSearch(text, dateFrom, dateTo,
                offset, itemPerPage, author, tag), HttpStatus.OK);
    }
}
