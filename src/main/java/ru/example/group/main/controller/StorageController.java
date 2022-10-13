package ru.example.group.main.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.example.group.main.dto.response.CommonResponseDto;
import ru.example.group.main.dto.response.ImageResponseDto;
import ru.example.group.main.exception.CloudinaryException;
import ru.example.group.main.service.CloudinaryService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;


@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/api/v1")
@Api("Image storage api")
public class StorageController {
    private final CloudinaryService cloudinaryService;

    @PostMapping("/storage")
    @ApiOperation("Operation to upload avatar image (as MultipartFile file IMAGE) to cloudinary service and provide back")
    public ResponseEntity<CommonResponseDto<ImageResponseDto>> downloadImage(@Valid @NotNull MultipartFile file) throws CloudinaryException {
        log.info("downloadImage started");
        return new ResponseEntity<>(cloudinaryService.uploadFileEndGetUrl(file), HttpStatus.OK);
    }
}
