package ru.example.group.main.dto.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ImageDto {
    private final String id;
    private final String url;
}
