package ru.example.group.main.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.example.group.main.dto.response.FriendsResponseDto;
import ru.example.group.main.dto.response.RecommendedFriendsResponseDto;
import ru.example.group.main.dto.response.ResultMessageDto;
import ru.example.group.main.entity.enumerated.FriendshipStatusType;
import ru.example.group.main.exception.FriendsRequestException;
import ru.example.group.main.exception.GetUserFriendsException;
import ru.example.group.main.exception.RecommendedFriendsLoadingFromDbToApiException;
import ru.example.group.main.service.FriendsService;
import ru.example.group.main.service.RecommendedFriendsService;

@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/api/v1")
@Api("Friendships api")
public class FriendsController {
    private final RecommendedFriendsService recommendedFriendsService;
    private final FriendsService friendsService;

    @GetMapping("/friends/recommendations")
    @ApiOperation("Operation to get recommended friends for current authorized user")
    public RecommendedFriendsResponseDto getRecommendedFriendsResponse(
            @RequestParam(required = false, defaultValue = "0") Integer offset,
            @RequestParam(required = false, defaultValue = "20") Integer itemPerPage) throws RecommendedFriendsLoadingFromDbToApiException {
        return recommendedFriendsService.getRecommendedFriendsResponse(offset, itemPerPage);
    }

    @GetMapping("/friends/recommendations_run")
    @ApiOperation("Operation to re-run recommendations process and update recommendations in DB for all users")
    public void getRecommendedFriendsResponse() {
        recommendedFriendsService.runMultithreadingFriendsRecommendationsUpdate();
    }

    @GetMapping("/friends")
    @ApiOperation("Operation to get friends for current authorized user or for user provided with parameter name (@RequestParam, required=false)")
    public FriendsResponseDto getUserFriends(@RequestParam(required = false, defaultValue = "name") String name,
                                             @RequestParam(required = false, defaultValue = "0") Integer offset,
                                             @RequestParam(required = false, defaultValue = "10") Integer itemPerPage) throws GetUserFriendsException {
        return friendsService.getUserFriends(name, offset, itemPerPage, FriendshipStatusType.FRIEND);
    }

    @GetMapping("/friends/request")
    @ApiOperation("Operation to get friends requests for current authorized user or for user provided with parameter name (@RequestParam, required=false)")
    public FriendsResponseDto getFriendsRequests(@RequestParam(required = false, defaultValue = "name") String name,
                                                 @RequestParam(required = false, defaultValue = "0") Integer offset,
                                                 @RequestParam(required = false, defaultValue = "10") Integer itemPerPage) throws GetUserFriendsException {
        return friendsService.getUserFriends(name, offset, itemPerPage, FriendshipStatusType.SUBSCRIBED);
    }

    @PostMapping("/friends/{id}")
    @ApiOperation("Operation to send friend request from current authorized user to id (@PathVariable) user.")
    public ResultMessageDto sendFriendRequest(@PathVariable Long id) throws FriendsRequestException {
        return friendsService.sendFriendRequest(id);
    }

    @DeleteMapping("/friends/{id}")
    @ApiOperation("Operation to set decline status from current authorized user for friend relation or request with id (@PathVariable) user.")
    public ResultMessageDto deleteFriend(@PathVariable Long id) throws FriendsRequestException {
        return friendsService.deleteOrBlockFriend(id, FriendshipStatusType.getLongFromEnum(FriendshipStatusType.DECLINED).intValue());
    }

    @PutMapping("/users/block/{id}")
    @ApiOperation("Operation to set blocked status from current authorized user to user with id (@PathVariable).")
    public ResultMessageDto blockUser(@PathVariable Long id) throws FriendsRequestException {
        return friendsService.deleteOrBlockFriend(id, FriendshipStatusType.getLongFromEnum(FriendshipStatusType.BLOCKED).intValue());
    }

}
