package ru.example.group.main.config;

import com.maxmind.db.Reader;
import com.maxmind.geoip2.DatabaseReader;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Configuration
public class GeoLocationConfig {

    @Bean
    public DatabaseReader databaseReader(ResourceLoader resourceLoader) {

        try {
            log.info("GeoLocationConfig: Trying to load GeoLite2-City database...");

            Resource resource = resourceLoader.getResource("classpath:maxmind/GeoLite2-City.mmdb");
            InputStream dbAsStream = resource.getInputStream();

            log.info("GeoLocationConfig: Database was loaded successfully.");

            return new DatabaseReader.Builder(dbAsStream).fileMode(Reader.FileMode.MEMORY).build();

        } catch (IOException | NullPointerException e) {
            log.error("Database reader cound not be initialized. ", e);
            return null;
        }
    }
}
