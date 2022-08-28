package ru.example.group.main.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.example.group.main.dto.response.CommonResponseDto;
import ru.example.group.main.dto.response.UrlImageResponseDto;
import ru.example.group.main.exception.CloudinaryException;
import ru.example.group.main.service.CloudinaryService;


@RestController
@Slf4j
public class StorageController {

    private final CloudinaryService cloudinaryService;

    public StorageController(CloudinaryService cloudinaryService) {
        this.cloudinaryService = cloudinaryService;
    }

    @PostMapping("/api/v1/storage")
    public ResponseEntity<CommonResponseDto<UrlImageResponseDto>> downloadImage(MultipartFile file) throws CloudinaryException {
        log.info("downloadImage started");
        return new ResponseEntity<>(cloudinaryService.uploadFileEndGetUrl(file), HttpStatus.OK);
    }
}
