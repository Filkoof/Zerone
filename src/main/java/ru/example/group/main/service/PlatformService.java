package ru.example.group.main.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.example.group.main.dto.LanguageDto;
import ru.example.group.main.response.ListResponseLanguageDto;

@Service
@RequiredArgsConstructor
public class PlatformService {

  public ListResponseLanguageDto getLange() {
    ListResponseLanguageDto listResponseLanguageDto = new ListResponseLanguageDto();
    listResponseLanguageDto.setTotal(2);
    listResponseLanguageDto.setPerPage(1);
    listResponseLanguageDto.setOffset(0);
    List<LanguageDto> language = new ArrayList<>();
    language.add(new LanguageDto());
    language.get(0).setId(1);
    language.get(0).setTitle("ru");
    listResponseLanguageDto.setTimestamp(LocalDateTime.now());
    listResponseLanguageDto.setError("");
    language.add(new LanguageDto());
    language.get(1).setId(2);
    language.get(1).setTitle("en");
    listResponseLanguageDto.setData(language);
    return listResponseLanguageDto;
  }

}
