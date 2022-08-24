package ru.example.group.main.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.example.group.main.dto.CommonResponseDto;
import ru.example.group.main.dto.UrlImageDto;
import ru.example.group.main.service.CloudinaryService;


@RestController
public class StorageController {

    private final CloudinaryService cloudinaryService;

    public StorageController(CloudinaryService cloudinaryService) {
        this.cloudinaryService = cloudinaryService;
    }

    @PostMapping("/api/v1/storage")
    public ResponseEntity<CommonResponseDto<UrlImageDto>> downloadImage(MultipartFile file) {
        CommonResponseDto<UrlImageDto> response = new CommonResponseDto<>();
        UrlImageDto urlImageDto = new UrlImageDto();
        urlImageDto.setUrl(cloudinaryService.uploadFile(file));
        response.setData(urlImageDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
