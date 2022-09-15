package ru.example.group.main.helper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import ru.example.group.main.service.RecommendedFriendsService;

@Slf4j
public class BeanInitMethodImpl {

    @Autowired
    private RecommendedFriendsService recommendedFriendsService;

    public void runFriendsRecommendationsUpdateAfterStartUp() {
        recommendedFriendsService.runMultithreadingFriendsRecommendationsUpdate();
        log.info("Friends recommendations updated after startup.");
    }
}
