package ru.example.group.main.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import ru.example.group.main.dto.LanguageDto;
import ru.example.group.main.response.ListResponseLanguageDto;

@Service
public class PlatformService {
  public ListResponseLanguageDto getLange(){
    ListResponseLanguageDto listResponseLanguageDto=new ListResponseLanguageDto();
    listResponseLanguageDto.setTotal(2);
    listResponseLanguageDto.setPerPage(1);
    listResponseLanguageDto.setOffset(0);
    List<LanguageDto>linguage=new ArrayList<>();
    linguage.add(new LanguageDto());
    linguage.get(0).setId(1);
    linguage.get(0).setTitle("ru");
    listResponseLanguageDto.setTimestamp(LocalDateTime.now());
    listResponseLanguageDto.setError("");
    linguage.add(new LanguageDto());
    linguage.get(1).setId(2);
    linguage.get(1).setTitle("en");
    listResponseLanguageDto.setData(linguage);
    return listResponseLanguageDto;
  }

}
