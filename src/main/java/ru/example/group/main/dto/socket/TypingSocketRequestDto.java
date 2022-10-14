package ru.example.group.main.dto.socket;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TypingSocketRequestDto {
    private Long author;
    private Long dialog;
}