package ru.example.group.main.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.example.group.main.dto.response.FriendsResponseDto;
import ru.example.group.main.dto.response.RecommendedFriendsResponseDto;
import ru.example.group.main.service.FriendsService;
import ru.example.group.main.service.RecommendedFriendsService;

@RestController
@Slf4j
public class FriendsController {

    private final RecommendedFriendsService recommendedFriendsService;
    private final FriendsService friendsService;

    public FriendsController(RecommendedFriendsService recommendedFriendsService, FriendsService friendsService) {
        this.recommendedFriendsService = recommendedFriendsService;
        this.friendsService = friendsService;
    }

    @GetMapping("/api/v1/friends/recommendations")
    public RecommendedFriendsResponseDto getRecommendedFriendsResponse(
            @RequestParam(required = false, defaultValue = "0") Integer offset,
            @RequestParam(required = false, defaultValue = "20") Integer itemPerPage){
        log.info("getRecommendedFriendsResponse started");

        return recommendedFriendsService.getRecommendedFriendsResponse(offset, itemPerPage);
    }

    @GetMapping("/api/v1/friends/recommendations_run")
    public void getRecommendedFriendsResponse(){
        log.info("recommended friends update run");
        recommendedFriendsService.runMultithreadingFriendsRecommendationsUpdate();
    }

    @GetMapping("/api/v1/friends")
    public FriendsResponseDto getUserFriends(@RequestParam(required = false) String name,
                                             @RequestParam(required = false, defaultValue = "0") Integer offset,
                                             @RequestParam(required = false, defaultValue = "10") Integer itemPerPage){
        return friendsService.getUserFriends(offset, itemPerPage);
    }

}
