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
    listResponseLanguageDto.setTotal(0);
    listResponseLanguageDto.setPerPage(0);
    listResponseLanguageDto.setOffset(0);
    List<LanguageDto>list=new ArrayList<>();
    list.add(new LanguageDto());
    list.get(0).setId(0);
    list.get(0).setTitle("string");
    listResponseLanguageDto.setData(list);
    listResponseLanguageDto.setTimestamp(LocalDateTime.now());
    listResponseLanguageDto.setError("string");
    return listResponseLanguageDto;
  }

}
