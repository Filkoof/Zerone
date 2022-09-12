package ru.example.group.main.repository.jdbc;

import ru.example.group.main.entity.UserEntity;

import java.util.List;
import java.util.Map;


public interface RecommendedFriendsPureRepository {

    int deleteAll();
    int deleteInactive();
    List<Long> getAllActiveUsersIds();
    List<Long> getRecommendedFriendsForUser(Long userId);
    int[] updateBatchRecommendationsArray(Map<Long, String> recommendedFriendsMapInt);
    int[] insertBatchRecommendationsArray(Map<Long, String> recommendedFriendsMapInt);
    //List<UserEntity> getRecommendedFriendsForAPI(Long userId);
    String getRecommendedFriendsStringForAPI(Long userId);


}
