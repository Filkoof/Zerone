package ru.example.group.main.repository.jdbc;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class RecommendedFriend {
    private Long id;
    private Long userId;
    private Long recommendedFriendId;

}
