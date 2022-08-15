package ru.example.group.main.response;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.example.group.main.dto.LanguageDto;
@RequiredArgsConstructor
@Data
public class CommonListResponseDto<T> {

  private int total;
  private int perPage;
  private int offset;
  private List<T> data;
  private String error;
  private LocalDateTime timestamp;
}
