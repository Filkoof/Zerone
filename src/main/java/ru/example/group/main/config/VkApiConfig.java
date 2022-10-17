package ru.example.group.main.config;

import com.vk.api.sdk.client.Lang;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.database.responses.GetCountriesResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VkApiConfig {

    @Value("${vk.userId}")
    private Integer userId;

    @Value("${vk.accessToken}")
    private String accessToken;

    @Value("${vk.deactivated}")
    private boolean deactivated;

    @Bean
    public UserActor userActor() {
        return new UserActor(userId, accessToken);
    }

    @Bean
    public VkApiClient vkApiClient() {
        return new VkApiClient(new HttpTransportClient());
    }

    @Bean
    public GetCountriesResponse getVkApiCountries() throws ClientException, ApiException {
        if (!deactivated) {
            return vkApiClient().database().getCountries(userActor()).lang(Lang.RU).needAll(true).count(235).execute();
        }
        return new GetCountriesResponse();
    }
}
