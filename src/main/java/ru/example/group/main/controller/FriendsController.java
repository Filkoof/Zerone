package ru.example.group.main.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.example.group.main.dto.response.RecommendedFriendsResponseDto;
import ru.example.group.main.service.FriendsService;

@RestController
public class FriendsController {

    private final FriendsService friendsService;

    public FriendsController(FriendsService friendsService) {
        this.friendsService = friendsService;
    }

    @GetMapping("/api/v1/friends/recommendations")
    public RecommendedFriendsResponseDto getRecommendedFriendsResponse(@RequestParam Integer offset, @RequestParam Integer itemPerPage){
        return friendsService.getRecommendedFriendsResponse(offset, itemPerPage);
    }
}
