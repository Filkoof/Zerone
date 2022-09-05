package ru.example.group.main.repository.jdbc;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class RecommendedFriendsMultithreadUpdate implements Runnable {


    private Map<Long, Long[]> recommendedFriendsMapInt;
    private JdbcRecommendedFriendsRepository jdbcRecommendedFriendsRepository;

    public RecommendedFriendsMultithreadUpdate(Map<Long, Long[]> recommendedFriendsMapInt, JdbcRecommendedFriendsRepository jdbcRecommendedFriendsRepository) {
        this.recommendedFriendsMapInt = recommendedFriendsMapInt;
        this.jdbcRecommendedFriendsRepository = jdbcRecommendedFriendsRepository;
    }

    @Override
    public void run() {
        Long start = System.currentTimeMillis();
        Map<Long, Long[]> recommendedFriendsMapIntSplit = new HashMap<>();
        int count = 0;
        int segmentLimit = recommendedFriendsMapInt.size() / 100 > 0 ? recommendedFriendsMapInt.size() / 100 : 10;
        Long userIdUpdateCount = 0L;
        int batchSize = 0;
        for (Long userId : recommendedFriendsMapInt.keySet()) {
            userIdUpdateCount++;
            recommendedFriendsMapIntSplit.put(userId, recommendedFriendsMapInt.get(userId));
            if (count == segmentLimit) {
                batchSize =+ jdbcRecommendedFriendsRepository.updateBatchRecommendationsArray(recommendedFriendsMapIntSplit).length;
                recommendedFriendsMapIntSplit = new HashMap<>();
                count = 0;
            }
            count++;
        }
        if (count != segmentLimit) {
            batchSize = +jdbcRecommendedFriendsRepository.updateBatchRecommendationsArray(recommendedFriendsMapIntSplit).length;
        }
        Long finish = System.currentTimeMillis();
        log.debug(userIdUpdateCount + " users, batch update: " + Long.toString(finish - start) + " millis, batch amount: " + batchSize);
    }
}
