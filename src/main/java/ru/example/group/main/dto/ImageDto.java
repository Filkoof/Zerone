package ru.example.group.main.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class ImageDto {

  private String id;
  private String url;
}
