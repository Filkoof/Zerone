package ru.example.group.main.response;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import ru.example.group.main.dto.LanguageDto;
@Data
public class CommonListResponseDto {
  private int total;
  private int perPage;
  private int offset;
  private List<LanguageDto> data;
  private String error;
  private LocalDateTime timestamp;

  public CommonListResponseDto() {
    total = 2;
    perPage = 1;
    offset = 0;
    data = new ArrayList<>();
    data.add(new LanguageDto(0,"ru"));
    data.add(new LanguageDto(1,"eng"));
    error = "error";
    timestamp = LocalDateTime.now();
  }


}
