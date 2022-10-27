package ru.example.group.main.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class LikeForStatsResponseDto {

    private String nameA;
    private String nameB;
    private int rate;

}
