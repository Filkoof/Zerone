package ru.example.group.main.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;
import ru.example.group.main.entity.enumerated.LikeType;

@Data
public class VoteRequestDto {
    @JsonAlias("item_id")
    private Long itemId;
    private LikeType type;
}
