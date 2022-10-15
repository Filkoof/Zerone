package ru.example.group.main.dto.socket;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TypingSocketResponseDto {
    private Long authorId;
    private String author;
    private Long dialog;
}
