package ru.example.group.main.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

@Data
public class LikesRequestDto {
    @JsonAlias("item_id")
    private Long itemId;
    private String type;
}
