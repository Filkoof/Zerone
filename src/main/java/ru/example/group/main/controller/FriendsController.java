package ru.example.group.main.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.example.group.main.dto.response.RecommendedFriendsResponseDto;
import ru.example.group.main.service.FriendsService;

@RestController
@Slf4j
public class FriendsController {

    private final FriendsService friendsService;

    public FriendsController(FriendsService friendsService) {
        this.friendsService = friendsService;
    }

    @GetMapping("/api/v1/friends/recommendations")
    public RecommendedFriendsResponseDto getRecommendedFriendsResponse(
            @RequestParam(required = false, defaultValue = "0") Integer offset,
            @RequestParam(required = false, defaultValue = "20") Integer itemPerPage){
        log.info("getRecommendedFriendsResponse started");
        return friendsService.getRecommendedFriendsResponse(offset, itemPerPage);
    }

}
