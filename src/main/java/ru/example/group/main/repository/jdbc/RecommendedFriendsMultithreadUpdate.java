package ru.example.group.main.repository.jdbc;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
public class RecommendedFriendsMultithreadUpdate {

    @Autowired
    private TaskExecutor taskExecutor;

    private JdbcRecommendedFriendsRepository jdbcRecommendedFriendsRepository;

    public RecommendedFriendsMultithreadUpdate(JdbcRecommendedFriendsRepository jdbcRecommendedFriendsRepository) {
        this.jdbcRecommendedFriendsRepository = jdbcRecommendedFriendsRepository;
    }

    public void runTasks (ArrayList<Map<Long, String>> listForThreading) {
        int i = 1;
        for (Map<Long, String> map : listForThreading) {
            taskExecutor.execute(getTask(map, i));
            i++;
        }
    }

    private Runnable getTask(Map<Long, String> recommendedFriendsMapInt, int i) {
        Long start = System.currentTimeMillis();
        //Map<Long, Long[]> recommendedFriendsMapIntSplit = new HashMap<>();
        Map<Long, String> recommendedFriendsMapIntSplit = new HashMap<>();
        int count = 0;
        int segmentLimit = recommendedFriendsMapInt.size() / 100 > 0 ? recommendedFriendsMapInt.size() / 100 : 10;
        Long userIdUpdateCount = 0L;
        int batchSize = 0;
        for (Long userId : recommendedFriendsMapInt.keySet()) {
            userIdUpdateCount++;
            recommendedFriendsMapIntSplit.put(userId, recommendedFriendsMapInt.get(userId));
            if (count == segmentLimit) {
                batchSize = +jdbcRecommendedFriendsRepository.updateBatchRecommendationsArray(recommendedFriendsMapIntSplit).length;
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
        return () -> {
            log.info(String.format("running update task %s. Thread: %s", i, Thread.currentThread().getName()));
        };
    }
}
