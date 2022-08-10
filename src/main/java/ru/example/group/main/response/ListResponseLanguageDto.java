package ru.example.group.main.response;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;
import ru.example.group.main.dto.LanguageDto;

@Data
public class ListResponseLanguageDto{
  private int total;
  private int perPage;
  private int offset;
  private List<LanguageDto> data;
  private String error;
  private LocalDateTime timestamp;
}
