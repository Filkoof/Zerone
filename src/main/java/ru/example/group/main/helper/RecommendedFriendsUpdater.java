package ru.example.group.main.helper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;
import ru.example.group.main.repository.jdbc.JdbcRecommendedFriendsRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RequiredArgsConstructor
@Component
@Slf4j
public class RecommendedFriendsUpdater {

    private final TaskExecutor taskExecutor;
    private final JdbcRecommendedFriendsRepository jdbcRecommendedFriendsRepository;

    public void runTasks(List<Map<Long, Long[]>> listForThreading) {
        int i = 1;
        for (Map<Long, Long[]> map : listForThreading) {
            taskExecutor.execute(getTask(map, i));
            i++;
        }
    }

    private Runnable getTask(Map<Long, Long[]> recommendedFriendsMapInt, int i) {
        Long start = System.currentTimeMillis();
        Map<Long, Long[]> recommendedFriendsMapIntSplit = new HashMap<>();
        int count = 0;
        int segmentLimit = recommendedFriendsMapInt.size() / 100 > 0 ? recommendedFriendsMapInt.size() / 100 : 10;
        Long userIdUpdateCount = 0L;
        int batchSize = 0;
        for (Map.Entry<Long, Long[]> entry : recommendedFriendsMapInt.entrySet()) {
            userIdUpdateCount++;
            recommendedFriendsMapIntSplit.put(entry.getKey(), entry.getValue());
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
        log.debug(userIdUpdateCount + " users, batch update: " + (finish - start) + " millis, batch amount: " + batchSize);
        return () -> log.info(String.format("running update task %s. Thread: %s", i, Thread.currentThread().getName()));
    }
}
