package ru.example.group.main.helper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import ru.example.group.main.service.RecommendedFriendsService;

@Slf4j
public class BeanInitMethod {

    @Value("${config.initRecommendations}")
    private boolean initRecommendations;

    @Autowired
    private RecommendedFriendsService recommendedFriendsService;

    public void runFriendsRecommendationsUpdateAfterStartUp() {
        if (initRecommendations) {
            recommendedFriendsService.runMultithreadingFriendsRecommendationsUpdate();
            log.info("Friends recommendations updated after startup.");
        }
    }
}
