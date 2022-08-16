package ru.example.group.main.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.example.group.main.dto.LanguageDto;
import ru.example.group.main.response.CommonListResponseDto;

@Service
@RequiredArgsConstructor
public class PlatformService {

  public CommonListResponseDto getLanguage() {
    //    listResponseLanguageDto.setTotal(2);
//    listResponseLanguageDto.setPerPage(1);
//    listResponseLanguageDto.setOffset(0);
//    listResponseLanguageDto.setData(new ArrayList<>());
//    listResponseLanguageDto.getData().add(new LanguageDto(0,"ru"));
//    listResponseLanguageDto.getData().add(new LanguageDto(1,"eng"));
//    listResponseLanguageDto.setTimestamp(LocalDateTime.now());
//    listResponseLanguageDto.setError("");
    return new CommonListResponseDto();
  }

}
